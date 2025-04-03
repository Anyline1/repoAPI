package ru.anyline.repoapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.service.UserProjectServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class UserProjectController {

    private final UserProjectServiceImpl userProjectService;

    public UserProjectController(UserProjectServiceImpl userProjectService) {
        this.userProjectService = userProjectService;
    }

    @PostMapping
    public ResponseEntity<UserProject> createProject(@RequestBody UserProject project) {
        UserProject createdProject = userProjectService.createUserProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProject> getProjectById(@PathVariable Long id) {
        return userProjectService.getUserProjectById(id)
                .map(project -> new ResponseEntity<>(project, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<UserProject>> getAllProjects() {
        List<UserProject> projects = userProjectService.getAllProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProject> updateProject(String id, UserProject project) {
        if (project == null || project.getName() == null || project.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long projectId;
        try {
            projectId = Long.parseLong(id);
            if (projectId <= 0) {
                return ResponseEntity.badRequest().build();
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        Optional<UserProject> updatedProject = userProjectService.updateUserProject(projectId, project);
        return updatedProject
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (userProjectService.deleteUserProject(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Optional<UserProject>> getProjectsByUserId(@PathVariable Long userId) {
        Optional<UserProject> projects = userProjectService.getUserProjectById(userId);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}