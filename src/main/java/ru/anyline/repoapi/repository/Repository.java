package ru.anyline.repoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anyline.repoapi.model.UserRepos;

import java.util.List;

public interface Repository extends JpaRepository<UserRepos, Long> {

    List<UserRepos> findByUsername(String username);
}
