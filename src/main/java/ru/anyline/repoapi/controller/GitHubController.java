package ru.anyline.repoapi.controller;

import ru.anyline.repoapi.model.Repository;
import ru.anyline.repoapi.service.GitHubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;


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
    public String getRepos(@RequestParam("username") String username) {
        return "redirect:/repos/" + username;
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