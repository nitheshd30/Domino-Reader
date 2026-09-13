import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

content = content.replace("                            )\n                            )\n\n                            HorizontalDivider()", "                            )\n\n                            HorizontalDivider()")

content = content.replace("androidx.compose.material.icons.Icons.Default.Info", "androidx.compose.material.icons.filled.Info")

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(content)
