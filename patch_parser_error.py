import re

with open("app/src/main/java/com/example/data/local/DominoBackupParser.kt", "r") as f:
    content = f.read()

# Let's fix parseZipBackup again. We modified it.
old_ret = """if (extractedLabels.isEmpty()) {
            throw IllegalArgumentException("No label files (.lbl) found in this ZIP.")
        }
        return ParsedBackupResult(backup, extractedLabels, extractedLogs)"""

new_ret = """if (extractedLabels.isEmpty()) {
            throw IllegalArgumentException("No label files (.lbl) found in this ZIP.")
        }
        return ParsedBackupResult(backup, extractedLabels, extractedLogs)"""

if old_ret in content:
    print("Found patched return")

