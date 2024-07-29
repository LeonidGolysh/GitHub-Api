package com.git.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.git.config.RestTemplateConfig;
import com.git.exception.RepositoryNotFoundException;
import com.git.exception.UserNotFoundException;
import com.git.model.Branch;
import com.git.model.GitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubServiceImpl implements GitHubService {
    private static final String GH_API_URL = "https://api.github.com";
    private static final Logger logger = LoggerFactory.getLogger(GitHubServiceImpl.class);

    @Autowired
    private RestTemplateConfig restTemplateConfig;

    public List<GitRepository> getUserRepo(String username) {
        JsonNode response = fetchUserRepos(username);
        return parseRepos(response);
    }

    public GitRepository getUserSpecificRepo(String username, String repoName) {
        JsonNode response = fetchRepoUser(username, repoName);
        return parsSingleRepo(response);
    }

    //================================User repositories==========================================

    private JsonNode fetchUserRepos(String username) {
        String url = GH_API_URL + "/user/" + username + "/repos";
        try {
            return restTemplateConfig.restTemplate().getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException e) {

            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new UserNotFoundException("User not found");
            } else {
                throw new RuntimeException("An error occurred: " + e.getMessage());
            }
        }
    }

    private List<GitRepository> parseRepos(JsonNode response) {
        if (response == null) {
            throw new UserNotFoundException("User not found");
        }

        List<GitRepository> repositories = new ArrayList<>();
        for (JsonNode repoNode : response) {
            try {
                GitRepository gitRepository = parsSingleRepo(repoNode);
                repositories.add(gitRepository);
            } catch (RepositoryNotFoundException e) {
                logger.warn("Ignored fork ar invalid repository: " + repoNode.get("name").asText());
            }
        }
        return repositories;
    }

    private List<Branch> getRepoBranches(String ownerLogin, String repoName) {
        JsonNode response = fetchRepoBranches(ownerLogin, repoName);
        return parsBranch(response);
    }

    private JsonNode fetchRepoBranches(String ownerLogin, String repoName) {
        String url = GH_API_URL + "/repos/" + ownerLogin + "/" + repoName + "/branches";
        try {
            return restTemplateConfig.restTemplate().getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException e) {

            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new RepositoryNotFoundException("Repository not found");
            } else {
                throw new RuntimeException("An error occurred: " + e.getMessage());
            }
        }
    }

    private List<Branch> parsBranch(JsonNode response) {
        if (response == null) {
            throw new RepositoryNotFoundException("Repository not found");
        }

        List<Branch> branches = new ArrayList<>();
        for (JsonNode branchNode : response) {
            String branchName = branchNode.get("name").asText();
            String lastCommit = branchNode.get("commit").get("sha").asText();
            Branch branch = new Branch(branchName, lastCommit);
            branches.add(branch);
        }
        return branches;
    }

    //===========================User specific repository=========================================

    private JsonNode fetchRepoUser(String username, String repoName) {
        String url = GH_API_URL + "/repos/" + username + "/" + repoName;
        try {
            return restTemplateConfig.restTemplate().getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException e) {

            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new RepositoryNotFoundException("Repository not found");
            } else {
                throw new RuntimeException("An error occurred: " + e.getMessage());
            }
        }
    }

    private GitRepository parsSingleRepo(JsonNode repoNode) {
        if (repoNode == null || repoNode.get("fork").asBoolean()) {
            throw new RepositoryNotFoundException("Repository not found or it is a fork");
        }

        String repoName = repoNode.get("name").asText();
        String ownerLogin = repoNode.get("owner").get("login").asText();

        List<Branch> branches = getRepoBranches(ownerLogin, repoName);
        return new GitRepository(repoName, ownerLogin, branches);
    }
}
