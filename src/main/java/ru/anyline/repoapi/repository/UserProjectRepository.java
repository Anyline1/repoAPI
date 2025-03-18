package ru.anyline.repoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.anyline.repoapi.model.UserProject;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, String> {
    List<UserProject> findByUserId(Long userId);

    List<UserProject> findByNameContainingIgnoreCase(String projectName);

    List<UserProject> findParticipantsByProjectId(Long projectId);

    void deleteById(Long id);

    Optional<UserProject> findById(Long id);

}
