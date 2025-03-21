package ru.anyline.repoapi.service;

import org.springframework.stereotype.Service;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.repository.UserProjectRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserProjectServiceImpl implements UserProjectService {
    private final UserProjectRepository userProjectRepository;

    public UserProjectServiceImpl(UserProjectRepository userProjectRepository) {
        this.userProjectRepository = userProjectRepository;
    }

    @Override
    public UserProject createUserProject(UserProject userProject) {
        return userProjectRepository.save(userProject);
    }

    @Override
    public Optional<UserProject> getUserProjectById(Long id) {
        return userProjectRepository.findById(id);
    }

    @Override
    public List<UserProject> getAllUserProjects(Long userId) {
        return userProjectRepository.findByUserId(userId);
    }

    @Override
    public Optional<UserProject> updateUserProject(Long id, UserProject project) {
        Optional<UserProject> existingProject = getUserProjectById(id);
        if (existingProject.isPresent()) {
            UserProject updatedProject = existingProject.get();
            updatedProject.setName(project.getName());
            updatedProject.setDescription(project.getDescription());

            return Optional.of(userProjectRepository.save(updatedProject));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteUserProject(Long id) {
        userProjectRepository.deleteById(id);
        return false;
    }

    @Override
    public List<UserProject> findUserProjectsByName(String projectName) {
        return userProjectRepository.findByNameContainingIgnoreCase(projectName);
    }

    @Override
    public void addParticipantToProject(Long projectId, Long userId) {
        UserProject project = getUserProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
        userProjectRepository.save(project);
    }

    @Override
    public void removeParticipantFromProject(Long projectId, Long userId) {
        UserProject project = getUserProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
        userProjectRepository.save(project);
    }

    @Override
    public List<UserProject> getProjectParticipants(Long projectId) {
        return userProjectRepository.findParticipantsByProjectId(projectId);
    }

    public List<UserProject> getAllProjects() {
        return userProjectRepository.findAll();
    }
}