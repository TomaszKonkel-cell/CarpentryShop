package com.example.carpentry.Service.Projects;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Repository.ProjectRepository;
import com.example.carpentry.Service.GoogleDriveApi.GoogleDriveService;
import com.example.carpentry.Service.Image.ImageServiceImpl;
import com.example.carpentry.Service.Order.ProjectsListServiceImpl;

@Service
public class ProjectsServiceImpl implements ProjectsService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectsListServiceImpl projectsListService;

    @Autowired
    ImageServiceImpl imageService;

    @Autowired
    GoogleDriveService googleDriveService;

    @Override
    public Optional<Project> getProject(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Optional<Project> findProject(String name) {
        return projectRepository.findByName(name);
    }

    @Override
    public boolean checkIfExist(Project project) {
        return projectRepository.existsById(project.getId());
    }

    @Override
    public ResponseEntity<?> createProject(Project project, MultipartFile file) throws GeneralSecurityException {
        if (findProject(project.getName()).isPresent()) {
            return new ResponseEntity<>("Istnieje projekt o tej samej nazwie", HttpStatus.BAD_REQUEST);
        }
        try {
            if (file != null) {
                File tempFile = File.createTempFile("temp", null);
                file.transferTo(tempFile);
                String imageName = imageService.saveImage(tempFile, project.getName());
                project.setPhoto(imageName);
            } else {
                project.setPhoto(null);
            }

            project.setStatus(true);
            projectRepository.save(project);
            return new ResponseEntity<>("Projekt stworzony pomyślnie", HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Coś poszło nie tak" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<?> updateProject(Long id, Project project, MultipartFile file)
            throws IOException, GeneralSecurityException {
        Optional<Project> projectData = getProject(id);
        Optional<Project> checkName = findProject(project.getName());

        if (checkName.isPresent() && checkName.get().getId() != id) {
            return new ResponseEntity<>("Podana nowa nazwa jest zajęta", HttpStatus.BAD_REQUEST);
        }

        if (projectData.isPresent()) {
            Project newProject = projectData.get();
            newProject.setPrice(project.getPrice());
            newProject.setDescription(project.getDescription());
            if (file != null) {
                // Konwersja z MultipartFile na File
                File tempFile = File.createTempFile("temp", null);
                file.transferTo(tempFile);
                if (!projectData.get().getName().equals(project.getName())) {
                    // Nowy folder, nowe zdjecie
                    newProject.setPhoto(
                            imageService.updateImage(tempFile, projectData.get().getName(), project.getName()));
                    newProject.setName(project.getName());
                    newProject.setStatus(newProject.isStatus());
                    projectRepository.save(newProject);
                    return new ResponseEntity<>(
                            "Projekt zaktualizowany z nową nazwą i plikiem: " + newProject.getName(), HttpStatus.OK);

                } else {
                    // Stary folder, nowe zdjęcie
                    newProject.setPhoto(imageService.updateImage(tempFile, projectData.get().getName(),
                            projectData.get().getName()));
                    newProject.setName(project.getName());
                    newProject.setStatus(newProject.isStatus());
                    projectRepository.save(newProject);
                    return new ResponseEntity<>("Projekt zaktualizowany z nowym plikiem", HttpStatus.OK);
                }
            }

            if (!projectData.get().getName().equals(project.getName())) {
                // Nowy folder, stare zdjęcie
                if (projectData.get().getPhoto() != null) {
                    imageService.moveImage(projectData.get().getName(), projectData.get().getPhoto(),
                            project.getName());
                    newProject.setPhoto(projectData.get().getPhoto());
                }else{
                    newProject.setPhoto(projectData.get().getPhoto());
                }

            } else {
                // Nie zmieniasz nazwy i nie wysyłasz zdjęcia
                newProject.setPhoto(projectData.get().getPhoto());
            }

            newProject.setName(project.getName());
            newProject.setStatus(newProject.isStatus());
            projectRepository.save(newProject);
            return new ResponseEntity<>("Projekt zaktualizowany", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Podany projekt nie istnieje", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<?> deleteProject(Long id) {
        Optional<Project> findProject = getProject(id);

        if (findProject.isPresent()) {
            if (!projectsListService.getProjectsListByProject(findProject.get()).isEmpty()) {
                findProject.get().setStatus(false);
                projectRepository.save(findProject.get());
                return new ResponseEntity<>("Projekt należy do jakiegoś zamówienia" + System.lineSeparator()
                        + "Projekt zostaje przeniesiony do zarchiwizowanych", HttpStatus.OK);
            } else {
                googleDriveService.deleteFolderFromDrive(findProject.get().getName());
                projectRepository.deleteById(id);
                return new ResponseEntity<>("Projekt usunięty", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Błąd!!! Podane zasoby do usunięcia nie istnieją", HttpStatus.BAD_REQUEST);

    }

    @Override
    public ResponseEntity<?> restoreProject(Long id) {
        Optional<Project> findProject = getProject(id);

        if (findProject.isPresent()) {
            findProject.get().setStatus(true);
            projectRepository.save(findProject.get());
            return new ResponseEntity<>("Projekt przywrócony do aktualnych", HttpStatus.OK);
        }
        return new ResponseEntity<>("Błąd!!! Podane zasoby do przywrócenia nie istnieją", HttpStatus.BAD_REQUEST);

    }

}
