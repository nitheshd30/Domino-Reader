import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

content = content.replace("onClick = { viewModel.toggleTheme() }", "onClick = { viewModel.toggleTheme(isCurrentlyDark) }")

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(content)
