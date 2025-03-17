package ru.anyline.repoapi.service;

import org.springframework.stereotype.Service;
import ru.anyline.repoapi.model.UserProject;
import ru.anyline.repoapi.model.User;
import ru.anyline.repoapi.repository.UserProjectRepository;
import ru.anyline.repoapi.repository.UserRepository;

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
    public UserProject updateUserProject(UserProject userProject) {
        if (userProjectRepository.existsById(userProject.getId())) {
            return userProjectRepository.save(userProject);
        }
        throw new IllegalArgumentException("Project not found with id: " + userProject.getId());
    }

    @Override
    public void deleteUserProject(Long id) {
        userProjectRepository.deleteById(id);
    }

    @Override
    public List<UserProject> findUserProjectsByName(String projectName) {
        return userProjectRepository.findByNameContainingIgnoreCase(projectName);
    }

    @Override
    public void addParticipantToProject(Long projectId, Long userId) {
        UserProject project = getUserProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
        User user = userProjectRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        project.getParticipants().add(user);
        userProjectRepository.save(project);
    }

    @Override
    public void removeParticipantFromProject(Long projectId, Long userId) {
        UserProject project = getUserProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        project.getParticipants().remove(user);
        userProjectRepository.save(project);
    }

    @Override
    public List<User> getProjectParticipants(Long projectId) {
        UserProject project = getUserProjectById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
        return project.getParticipants();
    }

    public List<UserProject> getAllProjects() {
        return userProjectRepository.findAll();
    }
}