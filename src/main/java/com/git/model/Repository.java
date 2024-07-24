package com.git.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Repository {
    private String repoName;
    private String ownerLogin;
    private List<Branch> listBranches;
}
