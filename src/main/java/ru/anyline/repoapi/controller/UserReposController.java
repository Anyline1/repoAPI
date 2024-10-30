package ru.anyline.repoapi.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.anyline.repoapi.exceptions.UserReposException;
import ru.anyline.repoapi.model.UserRepos;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserReposController {

    private final RestTemplate restTemplate;

    @GetMapping("/repos")
    public String getUserRepos(@RequestParam(name = "username", required = false) String username, Model model) {
        if (username != null && !username.isEmpty()) {
            String url = "http://localhost:8080/repos/" + username;
            try {
                ResponseEntity<UserRepos[]> responseEntity = restTemplate.getForEntity(url, UserRepos[].class);
                if (responseEntity.getStatusCode().is4xxClientError() || responseEntity.getStatusCode().is5xxServerError()) {
                    model.addAttribute("error", "Error 404: User not found or rate limit exceeded.");
                    return "repos";
                }
                UserRepos[] reposArray = responseEntity.getBody();

                if (reposArray != null) {
                    List<UserRepos> repos = List.of(reposArray);
                    model.addAttribute("repos", repos);
                }
                model.addAttribute("username", username);

            } catch (HttpClientErrorException e) {
                model.addAttribute("error", "Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            } catch (HttpServerErrorException e) {
                model.addAttribute("error", "Server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            } catch (ResourceAccessException e) {
                model.addAttribute("error", "Resource access error: Unable to connect to the server. Please try again later.");
            } catch (Exception e) {
                throw new UserReposException("An unexpected error occurred while fetching repositories.", e);
            }
        } else {
            model.addAttribute("error", "Username is required to fetch repositories.");
        }
        return "repos";
    }



}

