package com.tocgic.gitsvn.versioncontrolservice;

import java.util.ArrayList;

import com.tocgic.gitsvn.util.Out;

public class Git extends Vcs {
    private String branchName;

    public Git(String remoteUrl, String repoDirectory, String authUser, String authPass) {
        super(remoteUrl, repoDirectory, authUser, authPass);
    }

    @Override
    protected String getVcsName() {
        return "git";
    }

    @Override
    protected String getOptionNameUser() {
        return null;
    }

    @Override
    protected String getOptionNamePass() {
        return null;
    }

    @Override
    protected String run(String... commands) {
        ArrayList<String> params = new ArrayList<>();
        params.add("cd");
        params.add(repoDirectory);
        params.add("&&");
        for (String command : commands) {
            params.add(command);
        }
        String[] extendedCommands = params.toArray(new String[params.size()]);
        return super.run(extendedCommands);
    }

    /**
     * git clone https://username:password@github.com/username/repository.git
     */
    public String clone() {
        Out.println(Out.ANSI_GREEN, "... git clone "+remoteUrl+" "+repoDirectory);
        //TODO : remoteAuthUrl (add username & password)
        String remoteAuthUrl = this.remoteUrl;
        return run(makeParam("clone", remoteAuthUrl, repoDirectory));
    }

    /**
     * git checkout {branchName} -f
     */
    public String checkout(String branchName) {
        Out.println(Out.ANSI_GREEN, "... git checkout "+branchName+" -f");
        this.branchName = branchName;
        return run(makeParam("checkout", branchName));
    }
}