package com.git.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.git.model.Branch;
import com.git.model.GitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "application/json");

        RequestEntity<Void> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, URI.create(url));
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(requestEntity, JsonNode.class);

        if (responseEntity.getStatusCodeValue() == 404) {
            throw new Exception("User not found");
        }

        List<GitRepository> repositories = new ArrayList<>();
        for (JsonNode repoNode : responseEntity.getBody()) {
            if (!repoNode.get("fork").asBoolean()) {
                String repoName = repoNode.get("name").asText();
                String ownerLogin = repoNode.get("owner").asText();

                List<Branch> branches = getRepoBranches(ownerLogin, repoName);
                GitRepository gitRepository = new GitRepository(repoName, ownerLogin, branches);
                repositories.add(gitRepository);
            }
        }
        return repositories;
    }

    private List<Branch> getRepoBranches(String ownerLogin, String repoName) {
        String url = GH_API_URL + "/repos/" + ownerLogin + "/" + repoName + "/branches";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "application/json");

        RequestEntity<Void> requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, URI.create(url));
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(requestEntity, JsonNode.class);

        List<Branch> branches = new ArrayList<>();
        for (JsonNode branchNode : responseEntity.getBody()) {
            String branchName = branchNode.get("name").asText();
            String lastCommit = branchNode.get("commit").get("sha").asText();
            Branch branch = new Branch(branchName, lastCommit);
            branches.add(branch);
        }
        return branches;
    }
}
