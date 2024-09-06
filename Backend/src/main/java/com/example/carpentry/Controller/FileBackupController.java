package com.example.carpentry.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.carpentry.Service.BackupService.FileBackupService;
import com.example.carpentry.Service.Projects.ProjectsServiceImpl;
import com.example.carpentry.Service.Storage.StorageServiceImpl;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("api/fileBackup")
@CrossOrigin
public class FileBackupController {

    @Autowired
    ProjectsServiceImpl projectsService;

    @Autowired
    StorageServiceImpl storageService;

    @GetMapping("saveFile")
    public String saveFile() throws IOException, GeneralSecurityException {
        FileBackupService.saveFile();
        return "Plik zapisano pomyślnie";
    }

    @GetMapping("readFile")
    public ResponseEntity<?> readFile(@RequestParam String path)
            throws FileNotFoundException, IOException, GeneralSecurityException, ClassNotFoundException, MessagingException {
        return FileBackupService.readFile(path);
    }

    @GetMapping("getFileList")
    public Set<String> getFileList() throws IOException {
        return FileBackupService.fileList();
    }

}
