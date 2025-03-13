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
     * Scans the repository with static code analyzer PMD.
     * 
     * @return rule violations detected by PMD
     */
    public List<CodeSmell> scan(String repositoryDirectory) {
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

    public String name() {
        return name;
    }

    public String url() {
        return url;
    }

}
