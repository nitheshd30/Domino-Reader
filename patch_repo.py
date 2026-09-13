import re

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "r") as f:
    content = f.read()

old_err = """        if (firestore == null) return PrinterBackup(0, "Error", "Error", "Error", "Error", "Error", "Error", 0, 0)"""
new_err = """        if (firestore == null) {
            throw IllegalStateException("Firebase is not connected! Please check google-services.json and your network.")
        }"""

content = content.replace(old_err, new_err)

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "w") as f:
    f.write(content)
