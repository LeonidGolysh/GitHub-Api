package com.git.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.git.config.RestTemplateConfig;
import com.git.exception.RepositoryNotFoundException;
import com.git.exception.UserNotFoundException;
import com.git.model.Branch;
import com.git.model.GitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubServiceImpl implements GitHubService {
    private static final String GH_API_URL = "https://api.github.com";

    @Autowired
    private RestTemplateConfig restTemplateConfig;

    public List<GitRepository> getUserRepo(String username) {
        JsonNode response = fetchUserRepos(username);
        return parseRepos(response);
    }

    private JsonNode fetchUserRepos(String username) {
        String url = GH_API_URL + "/users/" + username + "/repos";
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
            if (!repoNode.get("fork").asBoolean()) {

                String repoName = repoNode.get("name").asText();
                String ownerLogin = repoNode.get("owner").get("login").asText();

                List<Branch> branches = getRepoBranches(ownerLogin, repoName);
                GitRepository gitRepository = new GitRepository(repoName, ownerLogin, branches);
                repositories.add(gitRepository);
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
}
