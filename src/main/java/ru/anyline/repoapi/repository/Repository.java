package ru.anyline.repoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.anyline.repoapi.model.UserRepos;
import java.util.List;

public interface Repository extends JpaRepository<UserRepos, String> {

    List<UserRepos> findByUsername(String s);
}
