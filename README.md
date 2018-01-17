## git2svn-java

Commit the contents of the git repository to svn.

## Usage

```shell
$ java -jar git2svn-java-{version}.jar {option:-debug} {gitDir} {svnDir}
```

```sh
$ java -jar git2svn-java-{version}.jar {option:-debug} {gitDir} {gitUrl} {svnDir} {svnUrl}
```

```sh
$ java -jar git2svn-java-{version}.jar {option:-debug} {gitDir} {svnDir} {svnUrl} {svnUser} {svnPass}
```

```sh
$ java -jar git2svn-java-{version}.jar {option:-debug} {gitDir} {gitUrl} {svnDir} {svnUrl} {svnUser} {svnPass}
```

### Supported Properties

| Name    | Description                          |
| ------- | ------------------------------------ |
| -debug  | Optional. Show more infomation.      |
| gitDir  | Git repository full path. (source)   |
| svnDir  | Svn repository full path. (target)   |
| gitUrl  | Git repository clone url. (with SSH) |
| svnUrl  | Svn repository url.                  |
| svnUser | Svn account ID.                      |
| svnPass | Svn account password.                |

### Jenkins & Gitlab Hook 설정

1. Jenkins 설정
   - Plugin 설치 (Jenkins 관리 > 플러그인 관리)
     GIT Plugin, GitLab Plugin
2. Freestyle project
   - 빌드유발 : Build when a change is pushed to GitLab. (GitLab Plugin 이 있어야 함)
3. GitLab
   - Project > Settings > Integrations : Add webhook
     - Push events : checked
     - SSL Verification : unchecked
     - URL : {빌드유발에서 생성된 URL}

### Jenkins git2svn 연동

- Build / Execute shell

  ```sh
  export LANG=en_US.UTF-8
  export LC_ALL=en_US.UTF-8
  java -jar git2svn-java-{version}.jar `pwd` "{gitUrl}" "{svnDir}" "{svnUrl}" "{svnId}" "{svnPassword}"
  ```

  ​