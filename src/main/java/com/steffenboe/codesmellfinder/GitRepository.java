package com.steffenboe.codesmellfinder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GitRepository {

    private final String name;
    private final PMDStaticCodeAnalyzer staticCodeAnalyzer;
    private final String url;

    private static final Logger LOG = LoggerFactory.getLogger(GitRepository.class);

    GitRepository(String name, String url, PMDStaticCodeAnalyzer pmdStaticCodeAnalyzer) {
        this.name = name;
        this.url = url;
        this.staticCodeAnalyzer = pmdStaticCodeAnalyzer;
    }

    /**
     * Scans the repository with static code analyzer PMD. Expects the repository to
     * be cloned to the directoryPath.
     * 
     * @param directoryPath path to the cloned repository, must not be empty
     * 
     * @return rule violations detected by PMD
     */
    List<CodeSmell> scan(String directoryPath) {
        List<CodeSmell> codeSmells = staticCodeAnalyzer.analyze(directoryPath);
        cleanUp(directoryPath);
        return codeSmells;

    }

    private void cleanUp(String directoryPath) {
        try {
            LOG.debug("Deleting {}", directoryPath);
            FileUtils.deleteDirectory(new File(directoryPath));
            LOG.debug("Deletion successful!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String name() {
        return name;
    }

    public String url() {
        return url;
    }

}
