package ru.anyline.repoapi.service;

import org.springframework.stereotype.Service;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.repository.UserProjectRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserProjectService implements UserProject {
    private final UserProjectRepository userProjectRepository;

    public UserProjectService(UserProjectRepository userProjectRepository) {
        this.userProjectRepository = userProjectRepository;
    }

    public UserProject createProject(UserProject project) {
        return userProjectRepository.save(project);
    }

    public Optional<UserProject> getProjectById(Long id) {
        return userProjectRepository.findById(id);
    }

    public List<UserProject> getAllProjects() {
        return userProjectRepository.findAll();
    }

    public Optional<UserProject> updateProject(Long id, UserProject project) {
        if (userProjectRepository.existsById(id)) {
            project.setId(id);
            return Optional.of(userProjectRepository.save(project));
        }
        return Optional.empty();
    }

    public boolean deleteProject(Long id) {
        if (userProjectRepository.existsById(id)) {
            userProjectRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<UserProject> getProjectsByUserId(Long userId) {
        return userProjectRepository.findByUserId(userId);
    }
}