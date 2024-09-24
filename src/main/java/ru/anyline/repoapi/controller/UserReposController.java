package ru.anyline.repoapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import ru.anyline.repoapi.model.UserRepos;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserReposController {

    private final RestTemplate restTemplate;

    @GetMapping("/repos")
    public String getUserRepos(@RequestParam(name = "username", required = false) String username, Model model) {
        if (username != null && !username.isEmpty()) {
            String url = "http://localhost:8080/repos/" + username; // URL вашего REST-контроллера
            try {
                ResponseEntity<UserRepos[]> responseEntity = restTemplate.getForEntity(url, UserRepos[].class);
                UserRepos[] reposArray = responseEntity.getBody();

                if (reposArray != null) {
                    List<UserRepos> repos = List.of(reposArray);
                    model.addAttribute("repos", repos);
                }
                model.addAttribute("username", username);
            } catch (Exception e) {
                model.addAttribute("error", "Error fetching repositories or rate limit exceeded.");
            }
        }

        return "repos";
    }


}

