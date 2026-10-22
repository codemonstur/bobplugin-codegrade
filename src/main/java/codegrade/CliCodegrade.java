package codegrade;

import jcli.annotations.CliOption;

/** The command line options of the codegrade Bob command. */
public final class CliCodegrade {

    @CliOption(name = 'f', longName = "format", defaultValue = "TEXT",
        description = "Report format: TEXT, JSON, XML, SARIF, HTML, PDF or GITHUB")
    public String format;

    @CliOption(name = 'b', longName = "fail-below", defaultValue = "",
        description = "Exit non-zero if the grade is below this threshold (a score 0-100)")
    public String failBelow;

    @CliOption(name = 'c', longName = "config", defaultValue = "",
        description = "Configuration file to read (default: codegrade.json in the project root)")
    public String config;

    @CliOption(name = 's', longName = "suppressions", defaultValue = "",
        description = "Suppression file to read (default: codegrade-suppressions.json in the project root)")
    public String suppressions;

    @CliOption(name = 'B', longName = "baseline", defaultValue = "",
        description = "Baseline file of known violations (default: codegrade-baseline.json in the project root)")
    public String baseline;

    public CliCodegrade() {
    }
}
