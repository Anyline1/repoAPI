package ru.anyline.repoapi.controller;

import org.springframework.web.servlet.ModelAndView;
import ru.anyline.repoapi.model.Repository;
import ru.anyline.repoapi.service.GitHubClient;
import ru.anyline.repoapi.service.GitHubClient.GitHubRepo;
import ru.anyline.repoapi.service.GitHubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;


import java.io.IOException;
import java.util.List;

@Controller
public class GitHubController {

    @Autowired
    private GitHubService gitHubService;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/repos")
    public ModelAndView getRepos(@RequestParam String username) throws IOException {

        List<Repository> repos = gitHubService.getRepositories(username);


        String reposJson = gitHubService.convertReposToJson(repos);

        ModelAndView modelAndView = new ModelAndView("repos");
        modelAndView.addObject("username", username);
        modelAndView.addObject("reposJson", reposJson);
        return modelAndView;
    }

    @GetMapping("/repos/{username}")
    public String showRepos(@PathVariable String username, Model model) throws JsonProcessingException {
        List<Repository> repos = gitHubService.getRepositories(username);
        String reposJson = objectMapper.writeValueAsString(repos);
        model.addAttribute("username", username);
        model.addAttribute("reposJson", reposJson);
        return "repos";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }


}