package com.steffenboe.codesmellfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GitRepository {

    private final String name;
    private final PMDStaticCodeAnalyzer staticCodeAnalyzer;
    private final String url;

    private static final Logger LOG = LoggerFactory.getLogger(GitRepository.class);

    GitRepository(String name, String url) {
        this.name = name;
        this.url = url;
        this.staticCodeAnalyzer = new PMDStaticCodeAnalyzer();
    }

    /**
     * Scans the repository with static code analyzer PMD.
     * 
     * @return rule violations detected by PMD
     */
    public List<CodeSmell> find() {
        String repositoryDirectory = cloneRepo();
        LOG.info("Cloned " + repositoryDirectory);
        List<CodeSmell> codeSmells = staticCodeAnalyzer.analyze(repositoryDirectory);
        try {
            LOG.debug("Deleting {}", repositoryDirectory);
            FileUtils.deleteDirectory(new File(repositoryDirectory));
            LOG.debug("Deletion successful!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return codeSmells;

    }

    private String cloneRepo() {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File file = Path.of(tmpdir, name).toFile();

        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        CloneCommand cloneCommand = Git
                .cloneRepository()
                .setDirectory(file)
                .setURI(url);

        try (Git git = cloneCommand.call()) {

        } catch (InvalidRemoteException invalidRemoteException) {
            LOG.error(invalidRemoteException.getMessage());
        } catch (TransportException transportException) {
            LOG.error(transportException.getMessage());
        } catch (GitAPIException gitAPIException) {
            LOG.error(gitAPIException.getMessage());
        }
        return file.getPath();
    }

    public String name() {
        return name;
    }

}
