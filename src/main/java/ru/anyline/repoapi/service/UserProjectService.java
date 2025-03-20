package ru.anyline.repoapi.service;

import ru.anyline.repoapi.model.UserProject;

import java.util.List;
import java.util.Optional;

public interface UserProjectService {

    UserProject createUserProject(UserProject userProject);

    Optional<UserProject> getUserProjectById(Long id);

    List<UserProject> getAllUserProjects(Long userId);

    UserProject updateUserProject(UserProject userProject);

    boolean deleteUserProject(Long id);

    List<UserProject> findUserProjectsByName(String projectName);

    void addParticipantToProject(Long projectId, Long userId);

    void removeParticipantFromProject(Long projectId, Long userId);

    List<UserProject> getProjectParticipants(Long projectId);
}