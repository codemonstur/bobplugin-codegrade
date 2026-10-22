package codegrade;

import codegrade.config.Baseline;
import codegrade.config.Config;
import codegrade.config.Suppressions;
import codegrade.core.Checklist;
import codegrade.core.Codebase;
import codegrade.core.Grader;
import codegrade.core.Metrics;
import codegrade.core.Threshold;
import codegrade.report.Report;
import codegrade.report.ReportFormat;
import bobthebuildtool.pojos.buildfile.Project;
import bobthebuildtool.pojos.error.PluginExecutionFailed;
import bobthebuildtool.pojos.error.VersionTooOld;
import bobthebuildtool.services.Log;
import jcli.errors.InvalidCommandLine;

import java.io.IOException;
import java.nio.file.Path;

import static bobthebuildtool.services.Functions.isNullOrEmpty;
import static bobthebuildtool.services.Update.requireBobVersion;
import static jcli.CliParserBuilder.newCliParser;

/**
 * A Bob plugin that runs the codegrade static analysis over a Java codebase and reports a
 * quality grade.
 */
public enum BobPlugin {;

    // The summary printed when the plugin is listed.
    private static final String DESCRIPTION =
        "Runs the codegrade static checks over a Java codebase and reports a quality grade";

    /**
     * Registers the {@code codegrade} command with the project.
     *
     * @param project the project to register the command on
     * @throws VersionTooOld when the running Bob version is too old
     */
    public static void installPlugin(final Project project) throws VersionTooOld {
        requireBobVersion("11");
        project.addCommand("codegrade", DESCRIPTION, BobPlugin::grade);
    }

    /**
     * Runs codegrade over the project and reports the result.
     *
     * @param project the project to grade
     * @param environment the environment variables of the build
     * @param args the command line arguments
     * @return zero on success
     * @throws InvalidCommandLine when the arguments cannot be parsed
     * @throws IOException when the project cannot be read
     * @throws PluginExecutionFailed when the grade is below the configured threshold
     */
    private static int grade(final Project project, final java.util.Map<String, String> environment,
            final String[] args) throws InvalidCommandLine, IOException, PluginExecutionFailed {
        final var previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(BobPlugin.class.getClassLoader());
        try {
            final var cli = newCliParser(CliCodegrade::new).parse(args);
            final var root = project.parentDir;

            final var config = Config.discover(root, isNullOrEmpty(cli.config) ? null : Path.of(cli.config));
            for (final var warning : config.warnings(Checklist.ids(), Metrics.ids())) {
                Log.logWarning(warning);
            }
            final var suppressions = Suppressions.discover(root,
                isNullOrEmpty(cli.suppressions) ? null : Path.of(cli.suppressions));
            final var baseline = Baseline.discover(root,
                isNullOrEmpty(cli.baseline) ? null : Path.of(cli.baseline));

            final var files = Codebase.collect(root);
            final var reportData = Codebase.grade(root.toString(), config,
                new Grader.Scope(root, suppressions, baseline), files);
            final var result = reportData.result();

            final var formats = ReportFormat.parseAll(cli.format);
            for (final var format : formats) {
                Log.logInfo(Report.render(reportData, format));
            }

            if (!Threshold.isPassing(result, cli.failBelow)) {
                throw new PluginExecutionFailed(
                    "CodeGrade " + String.format("%.1f", result.score())
                        + " is below the required threshold '" + cli.failBelow + "'", null);
            }
            return 0;
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }
    }
}
