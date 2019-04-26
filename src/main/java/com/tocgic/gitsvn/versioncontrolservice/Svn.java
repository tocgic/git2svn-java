package com.tocgic.gitsvn.versioncontrolservice;

import com.tocgic.gitsvn.util.Out;
import com.tocgic.gitsvn.util.RuntimeExecutor;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.Normalizer;
import java.util.ArrayList;

public class Svn extends Vcs {
    public Svn(String remoteUrl, String repoDirectory, String authUser, String authPass) {
        super(remoteUrl, repoDirectory, authUser, authPass);
    }
    
    @Override
    protected String getVcsName() {
        return "svn";
    }

    @Override
    protected String getOptionNameUser() {
        return "--username";
    }

    @Override
    protected String getOptionNamePass() {
        return "--password";
    }

    /**
     * $svn upgrade {target}
     * @param target
     * @return
     */
    public String upgrade(String target) {
        Out.println(Out.ANSI_GREEN, "... svn.upgrade(" + target + ")");
        ArrayList<String> list = new ArrayList<>();
        list.add(getVcsName());
        list.add("upgrade");
        list.add(target);
        return run(list);
    }

    /**
     * svn checkout ${SVN_URL} ${SVN_DIR}
     *
     * @param isForce : --force option
     */
    public String checkout(boolean isForce) {
        if (remoteUrl == null || remoteUrl.length() < 1) {
            return null;
        }
        Out.println(Out.ANSI_GREEN, "... svn.checkout("+isForce+")");
        if (isForce) {
            return run(makeParam("checkout", "--force", remoteUrl, "."));
        } else {
            return run(makeParam("checkout", remoteUrl, "."));
        }
    }

    /**
     * svn revert -R
     */
    public String revert() {
        Out.println(Out.ANSI_GREEN, "... svn.revert()");
        return run(makeParam("revert", ".", "-R"));
    }

    /**
     * svn cleanup --remove-unversioned
     */
    public String cleanup(boolean isRemoveUnversioned) {
        Out.println(Out.ANSI_GREEN, "... svn.cleanup(isRemoveUnversioned:"+isRemoveUnversioned+")");
        if (isRemoveUnversioned) {
            return run(makeParam("cleanup", "--remove-unversioned"));
        } else {
            return run(makeParam("cleanup"));
        }
    }

    /**
     * svn update
     */
    public String update() {
        Out.println(Out.ANSI_GREEN, "... svn.update()");
        return run(makeParam("update"));
    }

    /**
     * svn log --xml -l 1
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <log>
     * <logentry
     *    revision="17417">
     * <author>jycho</author>
     * <date>2018-01-08T09:56:29.737376Z</date>
     * <msg>2017/05/02 08:50:49 [djpark0402]: OneGuard 프래임워크 프로젝트 업로드
     * 
     * GitCommitHash:4e08059f7fa1965cd9d2f410ed998a489fd14ef1</msg>
     * </logentry>
     * </log>
     */
    public String getLastXmlLog() {
        Out.println(Out.ANSI_GREEN, "... svn.getLastXmlLog()");
        return run(makeParam("log", "--xml", "-l", "1"));
    }

    /**
     * svn status
     */
    public String status() {
        Out.println(Out.ANSI_GREEN, "... svn.status()");
//        return duplicatedCheck(run(makeParam("status", repoDirectory)));
        return run(makeParam("status", repoDirectory));
    }

    public String duplicatedCheck(String source) {
        if (source == null || source.length() < 1) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new StringReader(source));
        String line, prevLine = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                line = Normalizer.normalize(line, Normalizer.Form.NFC);

                if (prevLine != null) {
                    String[] items = line.split(" ");
                    String status = items[0];
                    String fileName = line.substring(status.length()).trim();

                    String[] prevItems = prevLine.split(" ");
                    String prevStatus = prevItems[0];
                    String prevFileName = prevLine.substring(prevStatus.length()).trim();

                    if (!prevFileName.equals(fileName)) {
                        sb.append(prevLine).append("\n");
                    } else {
                        if (prevStatus.startsWith("?")) {
                            line = prevStatus + line.substring(status.length());
                        }
                    }
                }
                prevLine = line;
            }
            if (prevLine != null) {
                sb.append(prevLine);
            }
        } catch (Exception e) {
            Out.println(Out.ANSI_RED, e.getMessage());
        }
        return sb.toString();
    }

    /**
     * svn add
     */
    public String add(String fileName) {
        Out.println(Out.ANSI_GREEN, "... svn.add("+fileName+")");
        int tryLimit = 2;
        if (fileName != null && fileName.length() > 0) {
            if (fileName.contains("@")) {
                fileName += "@";
            }
            do {
                tryLimit--;     
                String result = run(false, makeParam("add", "--force", fileName));
                // String result = return run(makeParam("add", fileName));
                if (!RuntimeExecutor.isErrorResponse(result)) {
                    return result;
                }
            } while (tryLimit > 0);
        }
        return null;
    }

    /**
     * svn rm
     */
    public String rm(String fileName) {
        Out.println(Out.ANSI_GREEN, "... svn.rm("+fileName+")");
        if (fileName != null && fileName.length() > 0) {
            if (fileName.contains("@")) {
                fileName += "@";
            }
            // return run(makeParam("rm", "\""+fileName+"\""));
            return run(false, makeParam("rm", fileName));
        }
        return null;
    }

    /**
     * svn commit -m {message}
     */
    public String commit(final String commitMessage) {
        String origin = commitMessage;
        if (origin == null) {
            origin = "";
        }
        origin = origin.replaceAll("\r", "\n");
        String messageHead = new String(origin).replaceAll("[\n ]+", " ").trim();
        messageHead = (messageHead.length() > 50) ? messageHead.substring(0, 50) + "..." : messageHead;
        Out.println(Out.ANSI_GREEN, "... svn.commit("+messageHead+")");
        return run(false, makeParam("commit", "-m", origin));
    }

    @Override
    protected boolean onHandledErrorByExecute(String output) {
        boolean isHandled = false;
        final String E000002 = "svn: E000002: Can't open directory '"; //svn: E000002: Can't open directory '{directory}': No such file or directory
        final String E120108 = "svn: E120108: Error running context: The server unexpectedly closed the connection.";
        final String E155004 = "svn: E155004:"; //svn: E155004: Run 'svn cleanup' to remove locks (type 'svn help cleanup' for details)
        final String E155036 = "svn: E155036: The working copy at '"; //svn: E155036: 'svn upgrade' 명령을 참고 하세요
        final String E200007 = "svn: E200007: Commit can only commit to a single repository at a time.";
        if (output.contains(E000002)) {
            String svnOutput = output.substring(output.indexOf(E000002));
            Out.println(Out.ANSI_PURPLE_BACKGROUND, svnOutput);
            /*
            svn: E000002: Can't open directory '{directory}': No such file or directory
            */
            String[] items = svnOutput.substring(E000002.length()).split("'");
            if (items.length > 0 && items[0].length() > 1) {
                String targetPath = items[0];
                File file = new File(targetPath);
                try {
                    if (isDirectoryWithName(targetPath)) {
                        isHandled = file.mkdirs();
                    } else {
                        isHandled = file.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (output.contains(E120108)) {
            String svnOutput = output.substring(output.indexOf(E120108));
            Out.println(Out.ANSI_PURPLE_BACKGROUND, svnOutput);
            Out.println(Out.ANSI_RED_BACKGROUND, "... retry");
            isHandled = true;
        } else if (output.contains(E155004)) {
            String svnOutput = output.substring(output.indexOf(E155004));
            Out.println(Out.ANSI_PURPLE_BACKGROUND, svnOutput);
            /*
            svn: E155004: Run 'svn cleanup' to remove locks (type 'svn help cleanup' for details)
            svn: E155004: 작업사본 '/Users/tocgic/Temp/git2svn/svn/iOS_mTransKey_2018'을(를) 잠궜습니다.
            svn: E155004: 경로('/Users/tocgic/Temp/git2svn/svn/iOS_mTransKey_2018')는 이미 잠겨 있습니다
            */
            cleanup(false);
            isHandled = true;
        } else if (output.contains(E155036)) {
            String svnOutput = output.substring(output.indexOf(E155036));
            Out.println(Out.ANSI_PURPLE_BACKGROUND, svnOutput);
            /*
            svn: E155036: 'svn upgrade' 명령을 참고 하세요
            svn: E155036: The working copy at '/Users/tocgic/Temp/git2svn/svn/iOS_mTransKey_2018/MTransKeyRes/iPhone_Res/dummy/alnum'
            is too old (format 10) to work with client version '1.11.1 (r1850623)' (expects format 31). You need to upgrade the working copy first.
            */
            String[] items = svnOutput.substring(E155036.length()).split("'");
            if (items.length > 0 && items[0].length() > 1) {
                String targetPath = items[0];
                String result = upgrade(targetPath);
                isHandled = !result.contains(RuntimeExecutor.RUNTIME_EXECUTOR_ERROR);
            }
        } else if (output.contains(E200007)) {
            String svnOutput = output.substring(output.indexOf(E155036));
            Out.println(Out.ANSI_PURPLE_BACKGROUND, svnOutput);
            /*
            svn: E200009: Commit failed (details follow):
            svn: E200009: '/Users/tocgic/Temp/git2svn/svn/iOS_mTransKey_2018/14:59:02' is not under version control
            For command line svn, I fixed this by running a find ./ -name ".*" to find those hidden files and maintain only the root .svn structure.
             */
            removeSvnSubDirectory();
            isHandled = true;
        }
        return isHandled;
    }

    /**
     * name 이름으로만 디렉토리 판단
     * @param targetPath
     * @return
     */
    public boolean isDirectoryWithName(String targetPath) {
        String[] fileItems = targetPath.split(File.separator);
        String endFileItem = fileItems[fileItems.length > 0 ? fileItems.length - 1 : fileItems.length];
        int index = endFileItem.lastIndexOf(".");
        return index < 1;
    }

    /**
     * repo 의 .svn 을 제외 한 subDirectory 내 .svn 디렉토리 제거
     * @return
     */
    public boolean removeSvnSubDirectory() {
        Out.println(Out.ANSI_GREEN, "... svn.removeSvnSubDirectory()");
        File root = new File(getRepoDirectory());
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (!".svn".equalsIgnoreCase(file.getName())) {
                    removeSvnDirectory(file);
                }
            }
        }
        return true;
    }

    private boolean removeSvnDirectory(File file) {
        if (file.isDirectory()) {
            if (".svn".equalsIgnoreCase(file.getName())) {
                try {
                    FileUtils.deleteDirectory(file);
                    Out.println(Out.ANSI_RED, "... remove : " + file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                File[] subFiles = file.listFiles();
                for (File subFile : subFiles) {
                    removeSvnDirectory(subFile);
                }
            }
        }
        return true;
    }
}