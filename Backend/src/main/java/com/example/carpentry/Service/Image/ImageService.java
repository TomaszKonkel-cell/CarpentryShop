package com.example.carpentry.Service.Image;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface ImageService {

    public String saveImage(File file, String projectName) throws IOException, GeneralSecurityException;

    public String updateImage(File file, String folderName, String newName) throws IOException, GeneralSecurityException;

    public void moveImage(String ProjectName,String folderId, String newName);

}
