package com.steffenboe.codesmellfinder;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.reporting.Report;

class PMDStaticCodeAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(PMDStaticCodeAnalyzer.class);

    /**
     * Executes a static code analysis on the given path.
     * 
     * @param path the path to analyze
     * @return list of detected "code smells"
     */
    List<CodeSmell> analyze(String path) {
        PMDConfiguration config = configurePmd(path);
        LOG.info("Analyzing {}...", path);
        return getCodeSmells(config);
    }

    List<CodeSmell> getCodeSmells(PMDConfiguration config) {
        List<CodeSmell> codeSmells;
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            codeSmells = getCodeSmellsFromPmdReport(pmd);
        }
        return codeSmells;
    }

    List<CodeSmell> getCodeSmellsFromPmdReport(PmdAnalysis pmd) {
        Report report = pmd.performAnalysisAndCollectReport();
        return report.getViolations().stream()
                .map(violation -> new CodeSmell(violation.getRule().getName()))
                .toList();
    }

    PMDConfiguration configurePmd(String repositoryDirectory) {
        PMDConfiguration config = new PMDConfiguration();
        config.setDefaultLanguageVersion(LanguageRegistry.PMD.getLanguageVersionById("java", "17"));
        config.addInputPath(Path.of(repositoryDirectory));
        config.setMinimumPriority(RulePriority.LOW);
        config.addRuleSet("src/main/resources/custom-ruleset.xml");
        config.setReportFormat("xml");
        config.setReportFile(Path.of("target/pmd-report.xml"));
        return config;
    }

}
