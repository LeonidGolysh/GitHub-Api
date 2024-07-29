# GitHub-Api

This is an application for obtaining information about the user's GitHub repositories using the GitHub API. 
The application provides information about all unfinished user repositories, as well as information about branches and the latest commits in these branches.

## Requirements

> - Java 21
> - Spring
> - Gradle
> - GitHub Personal Access Token

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/LeonidGolysh/GitHub-Api.git
   ```
3. Configure your GitHub Personal Access Token in the 'application.properties' file:
   ```properties
   github.token=your_personal_access_token
   ```

## Use

The application provides a REST API for obtaining information about the user's GitHub repositories.

Retrieving all user repositories:

* **URL:** /api/v1/user/{username}/repos
* **Method:** GET
* **Path Options:** `username` - GitHub username
* **Example of a query:**
> http://localhost:8080/api/v1/user/{username}/repos

Retrieving a specific user repository:

* **URL:** /api/v1/user/{username}/repos/{repoName}
* **Method:** GET
* **Path options:**
  * 'username' - GitHub username
  * 'repoName' - repository name
* **Example of a query:**
> http://localhost:8080/api/v1/user/{username}/repos/{repoName}
  
## Answer

A successful response for all repositories will contain a JSON array with repository information:
[
    {
        "repoName": "repository-name",
        "ownerLogin": "owner-login",
        "branches": [
            {
                "name": "branch-name",
                "lastCommit": "last-commit-sha"
            }
        ]
    }
]

A successful response for a specific repository will contain a JSON object with repository information:
{
    "repoName": "repository-name",
    "ownerLogin": "owner-login",
    "branches": [
        {
            "name": "branch-name",
            "lastCommit": "last-commit-sha"
        }
    ]
}

## Error handling

If the user or repository is not found, the application will return a JSON error:
{
    "path": "/api/v1/user/{username}/repos",
    "error": "User Not Found",
    "message": "User not found, please check your username",
    "timestamp": "2024-07-29T14:08:52.2796638",
    "status": 404
}
or
{
    "path": "/api/v1/user/{username}/repos/{repoName}",
    "error": "Repository Not Found",
    "message": "Repository not found, please check the name of the repository",
    "timestamp": "2024-07-29T14:09:50.4440481",
    "status": 404
}
