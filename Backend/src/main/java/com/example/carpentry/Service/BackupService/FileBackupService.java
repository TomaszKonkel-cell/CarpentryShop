package com.example.carpentry.Service.BackupService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Storage;
import com.example.carpentry.Service.GoogleDriveApi.GoogleDriveService;
import com.example.carpentry.Service.Projects.ProjectsServiceImpl;
import com.example.carpentry.Service.Storage.StorageServiceImpl;

import jakarta.mail.MessagingException;

@Service
public class FileBackupService {

    static ProjectsServiceImpl projectsService;

    static StorageServiceImpl storageService;

    static GoogleDriveService googleDriveService;

    @Autowired
    public FileBackupService(ProjectsServiceImpl projectsService, StorageServiceImpl storageService,
            GoogleDriveService googleDriveService) {
        FileBackupService.projectsService = projectsService;
        FileBackupService.storageService = storageService;
        FileBackupService.googleDriveService = googleDriveService;
    }

    private static final String backupFolderId = "1GBf0L-vsW9_vCBXaxRX3Bf2O7jxP0P0Z";

    public static void checkFolder()
            throws GeneralSecurityException, IOException {
        String projectsBackup = googleDriveService.findFolderIdOnDrive("project'sBackup");
        String storagesBackup = googleDriveService.findFolderIdOnDrive("storage'sBackup");

        if (projectsBackup == null) {
            googleDriveService.createFolderOnDrive("project'sBackup", backupFolderId);
        }
        if (storagesBackup == null) {
            googleDriveService.createFolderOnDrive("storage'sBackup", backupFolderId);
        }

    }

    public static void saveFile()
            throws IOException, GeneralSecurityException {
        checkFolder();

        List<Project> listOfProjects = projectsService.getProjects();
        List<Storage> listOfStorages = storageService.getItemsFromStorage();
        try {
            ObjectOutputStream fileWithProjects = new ObjectOutputStream(
                    new FileOutputStream("ListOfProjects - " + LocalDate.now() + ".txt"));
            fileWithProjects.writeObject(listOfProjects);

            File getFileProjects = new File("ListOfProjects - " + LocalDate.now() + ".txt");

            String folderProjectsId = googleDriveService.findFolderIdOnDrive("project'sBackup");
            googleDriveService.addToDrive("ListOfProjects - " + LocalDate.now() + ".txt", folderProjectsId,
                    "text/plain",
                    getFileProjects);
            fileWithProjects.close();

            getFileProjects.delete();

            ObjectOutputStream fileWithStorages = new ObjectOutputStream(
                    new FileOutputStream("ListOfStorages - " + LocalDate.now() + ".txt"));
            fileWithStorages.writeObject(listOfStorages);

            File getFileStorages = new File("ListOfStorages - " + LocalDate.now() + ".txt");

            String folderStoragesId = googleDriveService.findFolderIdOnDrive("storage'sBackup");
            googleDriveService.addToDrive("ListOfStorages - " + LocalDate.now() + ".txt", folderStoragesId,
                    "text/plain",
                    getFileStorages);
            fileWithStorages.close();

            getFileStorages.delete();
        } catch (IOException e) {
            System.err.println("Błąd przy zapisywaniu: " + e);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public static ResponseEntity<?> readFile(String path) throws MessagingException, FileNotFoundException, IOException,
            GeneralSecurityException, ClassNotFoundException {

        String fileBackupId = googleDriveService.findFolderIdOnDrive(path);
        List<String> listOfNames = new ArrayList<>();
        List<Project> readProjectsList = new ArrayList<>();
        List<Storage> readStoragesList = new ArrayList<>();

        if (path.equals("undefined")) {
            return new ResponseEntity<>("Nie został wybrany żaden plik do przywrócenia", HttpStatus.BAD_REQUEST);
        }

        if (path.contains("Projects")) {
            readProjectsList = (List<Project>) googleDriveService.getContentOfFile(fileBackupId, "Projects");
            readProjectsList.forEach((item) -> {
                try {
                    if (!projectsService.findProject(item.getName()).isPresent()) {
                        listOfNames.add(item.getName());
                        projectsService.createProject(item, null);
                    }

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            });
        }

        if (path.contains("Storages")) {
            readStoragesList = (List<Storage>) googleDriveService.getContentOfFile(fileBackupId, "Projects");
            readStoragesList.forEach((item) -> {
                if (!storageService.findItem(item.getItemName()).isPresent()) {
                    listOfNames.add(item.getItemName());
                    storageService.addItemStorage(item);
                }
            });
        }

        return new ResponseEntity<>(listOfNames, HttpStatus.OK);

    }

    public static Set<String> fileList() throws IOException {
        return googleDriveService.getAllBackupFile();

    }

}
