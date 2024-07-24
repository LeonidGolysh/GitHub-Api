package com.git.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GitRepository {
    private String repoName;
    private String ownerLogin;
    private List<Branch> listBranches;
}
