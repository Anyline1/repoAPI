package ru.anyline.repoapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.service.UserProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class UserProjectController {

    private final UserProjectService userProjectService;

    @Autowired
    public UserProjectController(UserProjectService userProjectService) {
        this.userProjectService = userProjectService;
    }

    @PostMapping
    public ResponseEntity<UserProject> createProject(@RequestBody UserProject project) {
        UserProject createdProject = userProjectService.createProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProject> getProjectById(@PathVariable Long id) {
        return userProjectService.getProjectById(id)
                .map(project -> new ResponseEntity<>(project, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<UserProject>> getAllProjects() {
        List<UserProject> projects = userProjectService.getAllProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProject> updateProject(@PathVariable Long id, @RequestBody UserProject project) {
        return userProjectService.updateProject(id, project)
                .map(updatedProject -> new ResponseEntity<>(updatedProject, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (userProjectService.deleteProject(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserProject>> getProjectsByUserId(@PathVariable Long userId) {
        List<UserProject> projects = userProjectService.getProjectsByUserId(userId);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}