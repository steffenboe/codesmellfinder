package com.steffenboe.codesmellfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CodeSmellScanner {

    private static final Logger LOG = LoggerFactory.getLogger(CodeSmellScanner.class);

    Optional<GitRepository> searchForCodeSmells(List<GitRepository> repositories) {
        for (GitRepository gitRepository : repositories) {
            LOG.info("Scanning repo {} for rule violations...", gitRepository.name());
            String repositoryDirectory = cloneRepo(gitRepository.name(), gitRepository.url());
            LOG.info("Cloned " + repositoryDirectory);
            if (gitRepository.scan(repositoryDirectory).size() > 0) {
                LOG.info("Found repo with rule violations: {}", gitRepository.url());
                return Optional.of(gitRepository);
            }
        }
        return Optional.empty();
    }

    private String cloneRepo(String name, String url) {
        String tmpdir = System.getProperty("user.home") + "/codesmellfinder/tmp";
        File file = Path.of(tmpdir, name).toFile();

        if (file.exists()) {
            delete(file);
        }
        executeCloneCommand(file, url);
        return file.getPath();
    }

    private void executeCloneCommand(File file, String url) {
        CloneCommand cloneCommand = Git
                .cloneRepository()
                .setDirectory(file)
                .setURI(url);
        call(cloneCommand);
    }

    private void call(CloneCommand cloneCommand) {
        try (Git git = cloneCommand.call()) {

        } catch (InvalidRemoteException invalidRemoteException) {
            LOG.error(invalidRemoteException.getMessage());
        } catch (TransportException transportException) {
            LOG.error(transportException.getMessage());
        } catch (GitAPIException gitAPIException) {
            LOG.error(gitAPIException.getMessage());
        }
    }

    private void delete(File file) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
