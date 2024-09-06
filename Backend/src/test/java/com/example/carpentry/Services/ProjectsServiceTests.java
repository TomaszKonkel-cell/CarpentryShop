package com.example.carpentry.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Repository.ProjectRepository;
import com.example.carpentry.Repository.OrderRepository.ProjectsListRepository;
import com.example.carpentry.Service.GoogleDriveApi.GoogleDriveService;
import com.example.carpentry.Service.Image.ImageServiceImpl;
import com.example.carpentry.Service.Order.ProjectsListServiceImpl;
import com.example.carpentry.Service.Projects.ProjectsServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectsServiceTests {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    ProjectsListRepository projectsListRepository;

    @Mock
    ImageServiceImpl imageService;

    @Mock
    ProjectsListServiceImpl projectsListService;

    @Mock
    GoogleDriveService googleDriveService;

    @InjectMocks
    ProjectsServiceImpl projectsService;

    private Project project;
    private MultipartFile file;

    @BeforeEach
    public void setup() {
        project = new Project(1L, "Krzesło", 160.00, "To jest krzesło", null, true);

        Path path = Paths.get("C:/Users/tkonk/Desktop/krzeslo-drewniane-walnut-new-wave.webp");
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }
        file = new MockMultipartFile(name,
                originalFileName, contentType, content);

    }

    @Test
    @Order(1)
    public void createProject_withoutImageFileAndUniqueName_thenCreateProject() throws GeneralSecurityException {

        ResponseEntity<?> response = projectsService.createProject(project, null);

        assertThat(response.getStatusCode().toString()).isEqualTo("201 CREATED");
        assertThat(response.getBody()).isEqualTo("Projekt stworzony pomyślnie");
    }

    @Test
    @Order(2)
    public void createProject_withoutImageFileAndExistName_thenResponseIsBadRequestWithCustomMessage()
            throws GeneralSecurityException {
        given(projectRepository.findByName(project.getName())).willReturn(Optional.of(project));

        ResponseEntity<?> response = projectsService.createProject(project, null);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Istnieje projekt o tej samej nazwie");
    }

    @Test
    @Order(3)
    public void createProject_withImageFileAndUniqueName_thenCreateProject() throws GeneralSecurityException {

        ResponseEntity<?> response = projectsService.createProject(project, file);

        assertThat(response.getStatusCode().toString()).isEqualTo("201 CREATED");
        assertThat(response.getBody()).isEqualTo("Projekt stworzony pomyślnie");
    }

    @Test
    @Order(4)
    public void createProject_withImageFileAndExistName_thenResponseIsBadRequestWithCustomMessage()
            throws GeneralSecurityException {
        given(projectRepository.findByName(project.getName())).willReturn(Optional.of(project));

        ResponseEntity<?> response = projectsService.createProject(project, file);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Istnieje projekt o tej samej nazwie");
    }

    @Test
    @Order(5)
    public void updateProject_withNewNameAndFile_thenUpdateProjectAndStatusIsOk()
            throws GeneralSecurityException, IOException {

        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        Project newProject = new Project(2L, "KrzesłoNew", 160.00, "To jest krzesło", null, true);

        ResponseEntity<?> response = projectsService.updateProject(project.getId(), newProject, file);

        assertThat(project.getName()).isEqualTo("KrzesłoNew");
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Projekt zaktualizowany z nową nazwą i plikiem: " + project.getName());
    }

    @Test
    @Order(6)
    public void updateProject_withOldNameAndNewFile_thenUpdateProjectAndStatusIsOk()
            throws GeneralSecurityException, IOException {

        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));

        ResponseEntity<?> response = projectsService.updateProject(project.getId(), project, file);

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Projekt zaktualizowany z nowym plikiem");
    }

    @Test
    @Order(7)
    public void updateProject_withOldNameAndNoFile_thenUpdateProjectAndStatusIsOk()
            throws GeneralSecurityException, IOException {

        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));

        ResponseEntity<?> response = projectsService.updateProject(project.getId(), project, null);

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Projekt zaktualizowany");
    }

    @Test
    @Order(8)
    public void updateProject_withNotFoundUser_thenResponseIsBadRequestWithCustomMessage()
            throws GeneralSecurityException, IOException {

        ResponseEntity<?> response = projectsService.updateProject(project.getId(), project, null);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Podany projekt nie istnieje");
    }

    @Test
    @Order(9)
    public void updateProject_withExistName_thenResponseIsBadRequestWithCustomMessage()
            throws GeneralSecurityException, IOException {

        Project existProject = new Project(2L, "KrzesłoNew", 160.00, "To jest krzesło", null, true);
        project.setName("KrzesłoNew");
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(projectRepository.findByName(project.getName())).willReturn(Optional.of(existProject));

        ResponseEntity<?> response = projectsService.updateProject(project.getId(), project, null);

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Podana nowa nazwa jest zajęta");
    }

    @Test
    @Order(10)
    public void deleteProject_withExistingInOrder_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {

        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));
        given(projectsListService.getProjectsListByProject(project)).willReturn(List.of(new ProjectsList()));

        ResponseEntity<?> response = projectsService.deleteProject(project.getId());

        assertThat(project.isStatus()).isFalse();
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Projekt należy do jakiegoś zamówienia" + System.lineSeparator()
                + "Projekt zostaje przeniesiony do zarchiwizowanych");
    }

    @Test
    @Order(11)
    public void deleteProject_withNotExistingInOrder_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {

        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));

        ResponseEntity<?> response = projectsService.deleteProject(project.getId());

        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Projekt usunięty");
    }

    @Test
    @Order(12)
    public void deleteProject_withNotExistingProject_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {

        ResponseEntity<?> response = projectsService.deleteProject(project.getId());

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Błąd!!! Podane zasoby do usunięcia nie istnieją");
    }

    @Test
    @Order(13)
    public void restoreProject_withExistingProject_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {
        given(projectRepository.findById(project.getId())).willReturn(Optional.of(project));

        ResponseEntity<?> response = projectsService.restoreProject(project.getId());

        assertThat(project.isStatus()).isTrue();
        assertThat(response.getStatusCode().toString()).isEqualTo("200 OK");
        assertThat(response.getBody()).isEqualTo("Projekt przywrócony do aktualnych");
    }

    @Test
    @Order(14)
    public void restoreProject_withNotExistingProject_thenResponseIsOKWithCustomMessage()
            throws GeneralSecurityException, IOException {

        ResponseEntity<?> response = projectsService.restoreProject(project.getId());

        assertThat(response.getStatusCode().toString()).isEqualTo("400 BAD_REQUEST");
        assertThat(response.getBody()).isEqualTo("Błąd!!! Podane zasoby do przywrócenia nie istnieją");
    }
}
