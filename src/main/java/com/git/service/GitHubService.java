package com.git.service;

import com.git.model.GitRepository;

import java.util.List;

public interface GitHubService {
    List<GitRepository> getUserRepo(String username);
    GitRepository getUserSpecificRepo(String username, String repoName);
}
