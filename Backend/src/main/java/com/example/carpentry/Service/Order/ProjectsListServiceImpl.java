package com.example.carpentry.Service.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.carpentry.Model.Project;
import com.example.carpentry.Model.Storage;
import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Model.Order.Resources;
import com.example.carpentry.Repository.OrderRepository.ProjectsListRepository;
import com.example.carpentry.Repository.OrderRepository.ResourcesRepository;
import com.example.carpentry.Service.Storage.StorageServiceImpl;

@Service
public class ProjectsListServiceImpl implements ProjectsListService {
    @Autowired
    ProjectsListRepository projectsListRepository;

    @Autowired
    ResourcesRepository resourcesRepository;

    @Autowired
    StorageServiceImpl storageService;

    @Autowired
    OrderServiceImpl orderService;

    @Override
    public Optional<ProjectsList> getProjectsList(Long id) {
        return projectsListRepository.findById(id);
    }

    @Override
    public List<ProjectsList> getProjectsListByProject(Project project) {
        return projectsListRepository.findAllByProject(project);
    }

    @Override
    public List<Resources> getResourcesByStorage(Storage item) {
        return resourcesRepository.findAllByItem(item);
    }

    @Override
    public ResponseEntity<?> addResource(Long id, List<Resources> resources) {
        if (checkData(id, resources).getStatusCode().is2xxSuccessful()) {
            Optional<ProjectsList> projectsList = projectsListRepository.findById(id);
            resources.forEach(i -> {
                changeQuantity(i.getItem().getId(), i.getQuantity());
                Resources resource = new Resources(i.getQuantity(), i.getItem(), projectsList.get());
                resourcesRepository.save(resource);
            });
            projectsList.get().setResources(resources);
            return new ResponseEntity<>("Dodano zasoby", HttpStatus.OK);
        } else {
            return checkData(id, resources);
        }

    }

    @Override
    public ResponseEntity<?> checkData(Long id, List<Resources> resources) {
        List<Long> ids = new ArrayList<>();
        if (!getProjectsList(id).isPresent()) {
            return new ResponseEntity<>("Pozycja z zamówienia nie istnieje", HttpStatus.BAD_REQUEST);
        }
        if (getProjectsList(id).get().getResources().stream().findAny().isPresent()) {
            return new ResponseEntity<>("Pozycja jest już uzupełniona", HttpStatus.BAD_REQUEST);
        }
        if (!getProjectsList(id).get().getCart().isPaid()) {
            return new ResponseEntity<>("Nie można zapisać zasobów do bazy danych, bo zamówienie nie jest opłacone",
                    HttpStatus.BAD_REQUEST);
        }
        if (resources.size() == 0) {
            return new ResponseEntity<>("Brak przedmiotów w liście", HttpStatus.BAD_REQUEST);
        } else {
            for (Resources i : resources) {
                ids.add(i.getItem().getId());
                if (i.getItem().getQuantity() < i.getQuantity()) {
                    return new ResponseEntity<>("Nie wystarczająca ilość przedmiotu: " + i.getItem().getItemName(),
                            HttpStatus.BAD_REQUEST);
                }
                if (!storageService.checkIfExist(i.getItem())) {
                    return new ResponseEntity<>("Podany przedmiot nie jest obecny", HttpStatus.BAD_REQUEST);
                }
                if (i.getQuantity() <= 0) {
                    return new ResponseEntity<>("Parametry liczbowe muszą być obecne i dodatnie",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }
        List<Long> checkDuplicates = ids.stream().distinct().collect(Collectors.toList());

        if (ids.size() != checkDuplicates.size()) {
            return new ResponseEntity<>("W żądaniu znajdują się powtórzone przedmioty", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void changeQuantity(Long id, int quantity) {
        Storage item = storageService.findItemById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przedmiotu"));

        if ( quantity > item.getQuantity())  {
            throw new RuntimeException("Nie wystarczająca ilość przedmiotu: " + item.getItemName());
        }
        
        item.setQuantity(item.getQuantity() - quantity);
    }

    @Override
    public ResponseEntity<?> deleteResources(Long projectsListId) {
        ProjectsList projectList = getProjectsList(projectsListId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pozycji"));

        List<Resources> projectListResources = projectList.getResources();

        projectListResources.forEach((a) -> {
            resourcesRepository.deleteById(a.getId());
            storageService.changeItemStorageQuantity(a.getItem(), a.getQuantity());
        });
        orderService.changeOrderStatus(projectList.getCart().getId());
        return new ResponseEntity<>("Zasoby usunięte z bazy, a zlecenie przywrócono", HttpStatus.OK);
    }

}
