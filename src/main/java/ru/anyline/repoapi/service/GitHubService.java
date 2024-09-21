package ru.anyline.repoapi.service;

import ru.anyline.repoapi.model.UserRepos;

import java.util.List;

public interface GitHubService {

    List<UserRepos> getRepositories(String username);
    List<UserRepos> getCachedRepos();
    UserRepos getRepository(String username, String reponame);

}
