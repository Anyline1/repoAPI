package ru.anyline.repoapi.service;

import java.util.List;
import java.util.Optional;

public interface UserProjectService {

    UserProject createUserProject(UserProject userProject);

    Optional<UserProject> getUserProjectById(Long id);

    List<UserProject> getAllUserProjects(Long userId);

    UserProject updateUserProject(UserProject userProject);

    void deleteUserProject(Long id);

    List<UserProject> findUserProjectsByName(String projectName);

    void addParticipantToProject(Long projectId, Long userId);

    void removeParticipantFromProject(Long projectId, Long userId);

    List<User> getProjectParticipants(Long projectId);
}