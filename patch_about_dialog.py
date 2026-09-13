import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

about_dialog_code = """
        // About Dialog
        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { showAboutDialog = false }
            )
        }
"""

content = content.replace("        // Clear All Entries Confirmation Dialog\n        if (showClearConfirmDialog) {", about_dialog_code + "\n        // Clear All Entries Confirmation Dialog\n        if (showClearConfirmDialog) {")

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(content)
