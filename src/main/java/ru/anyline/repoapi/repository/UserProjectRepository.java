package ru.anyline.repoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anyline.repoapi.model.UserProject;

public interface UserProjectRepository extends JpaRepository<UserProject, String> {
}
