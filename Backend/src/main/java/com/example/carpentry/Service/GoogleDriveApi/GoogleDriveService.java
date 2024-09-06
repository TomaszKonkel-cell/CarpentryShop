package com.example.carpentry.Service.GoogleDriveApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Storage;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

@SuppressWarnings("deprecation")
@Service
public class GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACOUNT_KEY_PATH = getPathToGoodleCredentials();

    private static String getPathToGoodleCredentials() {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, "cred.json");
        return filePath.toString();
    }

    public Drive createDriveService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName("DrivePhoto")
                .build();

    }

    public com.google.api.services.drive.model.File createFolderOnDrive(String folderName, String parentFolderId)
            throws GeneralSecurityException, IOException {
        Drive drive = createDriveService();
        com.google.api.services.drive.model.File dir = new com.google.api.services.drive.model.File();
        dir.setName(folderName);
        dir.setParents(Collections.singletonList(parentFolderId));
        dir.setMimeType("application/vnd.google-apps.folder");
        com.google.api.services.drive.model.File createdDir = drive.files().create(dir).execute();

        return createdDir;

    }

    public com.google.api.services.drive.model.File addToDrive(String fileName, String parentFolderId, String type, File file)
            throws GeneralSecurityException, IOException {
        Drive drive = createDriveService();
        com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
        fileMetaData.setName(fileName);
        fileMetaData.setParents(Collections.singletonList(parentFolderId));
        FileContent mediaContent = new FileContent(type, file);

        com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                .setFields("id").execute();

        return uploadedFile;

    }

    public String findFolderIdOnDrive(String folderName) {
        String folderId = null;
        try {
            Drive drive = createDriveService();
            FileList fileList = drive.files().list().setFields("files(id, name)").execute();
            for (com.google.api.services.drive.model.File file : fileList.getFiles()) {
                if (file.getName().equals(folderName)) {
                    folderId = file.getId();
                }
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folderId;
    }

    public Set<String> getAllBackupFile() {
        Set<String> allFiles = new HashSet<>();
        try {
            Drive drive = createDriveService();
            FileList fileList = drive.files().list().setFields("files(id, name)").execute();
            for (com.google.api.services.drive.model.File file : fileList.getFiles()) {
                if (file.getName().contains("ListOfProjects") || file.getName().contains("ListOfStorages")) {
                    allFiles.add(file.getName());
                }

            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allFiles;
    }

    @SuppressWarnings({ "unchecked", "resource" })
    public List<?> getContentOfFile(String fileBackupId, String type)
            throws IOException, GeneralSecurityException, ClassNotFoundException {

        Drive drive = createDriveService();
        OutputStream createTemp = new FileOutputStream("tmp.txt");

        drive.files().get(fileBackupId)
                .executeMediaAndDownloadTo(createTemp);

        ObjectInputStream data = new ObjectInputStream(new FileInputStream("tmp.txt"));
        File fileToDelete = new File("tmp.txt");

        if (type.equals("Projects")) {
            List<Project> readProjectsList = (List<Project>) data.readObject();

            createTemp.flush();
            createTemp.close();
            data.close();
            fileToDelete.delete();

            return readProjectsList;
        }

        if (type.equals("Storages")) {
            List<Storage> readStorageList = (List<Storage>) data.readObject();

            createTemp.flush();
            createTemp.close();
            data.close();
            fileToDelete.delete();

            return readStorageList;
        }

        return null;

    }

    public void deleteFolderFromDrive(String folderName) {

        try {
            Drive drive = createDriveService();
            FileList fileList = drive.files().list().setFields("files(id, name)").execute();
            for (com.google.api.services.drive.model.File file : fileList.getFiles()) {
                if (file.getName().equals(folderName)) {
                    drive.files().delete(file.getId()).execute();
                }
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
