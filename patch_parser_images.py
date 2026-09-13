import re

with open("app/src/main/java/com/example/data/local/DominoBackupParser.kt", "r") as f:
    content = f.read()

# Fix the condition to NOT include anything under IMAGE/ and to strictly match .lbl or .lnl
old_cond = """if (!entry.isDirectory && (name.endsWith(".lbl", ignoreCase = true) || name.endsWith(".lnl", ignoreCase = true) || name.contains("Labels/", ignoreCase = true))) {"""
new_cond = """if (!entry.isDirectory && (name.endsWith(".lbl", ignoreCase = true) || name.endsWith(".lnl", ignoreCase = true)) && !name.contains("IMAGE/", ignoreCase = true)) {"""

if old_cond in content:
    content = content.replace(old_cond, new_cond)
    with open("app/src/main/java/com/example/data/local/DominoBackupParser.kt", "w") as f:
        f.write(content)
    print("Patched zip extraction condition.")
else:
    print("Could not find the condition.")

