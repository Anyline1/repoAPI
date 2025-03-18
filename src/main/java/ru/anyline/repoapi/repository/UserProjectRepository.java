package ru.anyline.repoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anyline.repoapi.model.UserProject;

import java.util.List;

public interface UserProjectRepository extends JpaRepository<UserProject, String> {
    List<UserProject> findByUserId(Long userId);

    List<UserProject> findByNameContainingIgnoreCase(String projectName);
}
