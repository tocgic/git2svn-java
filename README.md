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
