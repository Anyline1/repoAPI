package ru.anyline.repoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anyline.repoapi.model.Repository;

import java.util.List;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    List<Repository> findByUsername(String username);
}
