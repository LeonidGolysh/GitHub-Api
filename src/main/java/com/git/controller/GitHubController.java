package com.git.controller;

import com.git.model.ErrorResponse;
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
@RequestMapping("/repos")
public class GitHubController {

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserRepos(@PathVariable String username) {
        try {
            List<GitRepository> repositories = gitHubService.getUserRepo(username);
            return ResponseEntity.ok(repositories);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage()));
        }
    }
}