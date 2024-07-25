package com.git.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.git.config.RestTemplateConfig;
import com.git.model.Branch;
import com.git.model.GitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubService {
    private static final String GH_API_URL = "https://api.github.com";

    @Autowired
    private RestTemplate restTemplate;

    public List<GitRepository> getUserRepo(String username) throws Exception {
        String url = GH_API_URL + "/users/" + username + "/repos";

        JsonNode response;

        try {
            response = restTemplate.getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 404) {
                throw new Exception("User not found");
            } else {
                throw new Exception("An error occurred: " + e.getMessage());
            }
        }

        if (response == null) {
            throw new Exception("User not found");
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

    private List<Branch> getRepoBranches(String ownerLogin, String repoName) throws Exception{
        String url = GH_API_URL + "/repos/" + ownerLogin + "/" + repoName + "/branches";
        JsonNode response;

        try {
            response = restTemplate.getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException e) {
            throw new Exception("Repository not found");
        }

        if (response == null) {
            throw new Exception("Repository not found");
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
