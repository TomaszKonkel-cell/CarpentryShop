package com.example.carpentry.Service.Image;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.carpentry.Service.GoogleDriveApi.GoogleDriveService;
import com.google.api.services.drive.Drive;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    GoogleDriveService googleDriveService;

    private static final String imageFolderId = "1QZTPHrwRwAI8wfJrebN344GgObPzatRV";

    @Override
    public String saveImage(File file, String folderName) throws IOException, GeneralSecurityException {

        com.google.api.services.drive.model.File createdDir = googleDriveService.createFolderOnDrive(folderName,
                imageFolderId);
        com.google.api.services.drive.model.File uploadedFile = googleDriveService.addToDrive("ImageForProject",
                createdDir.getId(), "image/jpeg",
                file);

        return uploadedFile.getId();

    }

    @Override
    public String updateImage(File file, String oldFolderName, String newFolderName)
            throws IOException, GeneralSecurityException {

        googleDriveService.deleteFolderFromDrive(oldFolderName);

        com.google.api.services.drive.model.File createdDir = googleDriveService.createFolderOnDrive(newFolderName,
                imageFolderId);
        com.google.api.services.drive.model.File uploadedFile = googleDriveService.addToDrive("ImageForProject",
                createdDir.getId(), "image/jpeg",
                file);

        return uploadedFile.getId();
    }

    @Override
    public void moveImage(String oldFolderName, String imageId, String newFolderName) {

        try {
            Drive drive = googleDriveService.createDriveService();
            com.google.api.services.drive.model.File createdNewDir = googleDriveService.createFolderOnDrive(
                    newFolderName,
                    imageFolderId);
            com.google.api.services.drive.model.File foundFileById = drive.files().get(imageId)
                    .setFields("parents")
                    .execute();

            StringBuilder previousParents = new StringBuilder();
            for (String parent : foundFileById.getParents()) {
                previousParents.append(parent);
            }

            foundFileById = drive.files().update(imageId, null)
                    .setAddParents(createdNewDir.getId())
                    .setRemoveParents(previousParents.toString())
                    .execute();

            googleDriveService.deleteFolderFromDrive(oldFolderName);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
