# CodeGrade plugin for bob

A plugin for [bob](https://github.com/codemonstur/bobthebuildtool) that runs the
[codegrade](https://github.com/codemonstur/codegrade) static analysis over a project and
reports a quality grade.

## Usage

```
bob codegrade
```

Options:

| Option | Description |
| --- | --- |
| `-f`, `--format` | Report format: `TEXT`, `JSON`, `XML`, `SARIF`, `HTML`, `PDF` or `GITHUB` (default `TEXT`). |
| `-b`, `--fail-below` | Exit non-zero if the grade is below this threshold (a score `0`–`100`). |
| `-c`, `--config` | Configuration file to read (default: `codegrade.json` in the project root). |
| `-s`, `--suppressions` | Suppression file to read (default: `codegrade-suppressions.json` in the project root). |
| `-B`, `--baseline` | Baseline file of known violations (default: `codegrade-baseline.json` in the project root). |

## Build

```
mvn clean package
```

The plugin jar is written to `target/bobplugin-codegrade.jar` and is loaded by bob through
the `plugin` directive in a project's `bobfile`:

```
plugin path/to/bobplugin-codegrade.jar
```
