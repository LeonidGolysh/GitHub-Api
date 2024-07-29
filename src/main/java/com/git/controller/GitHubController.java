package com.git.controller;

import com.git.model.GitRepository;
import com.git.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class GitHubController {

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/user/{username}/repos")
    public ResponseEntity<List<GitRepository>> getUserRepos(@PathVariable String username) {
            List<GitRepository> repositories = gitHubService.getUserRepo(username);
            return ResponseEntity.ok(repositories);
    }

    @GetMapping("user/{username}/repos/{repoName}")
    public ResponseEntity<GitRepository> getUserSpecificRepo(@PathVariable String username, @PathVariable String repoName) {
        GitRepository repository = gitHubService.getUserSpecificRepo(username, repoName);
        return  ResponseEntity.ok(repository);
    }
}