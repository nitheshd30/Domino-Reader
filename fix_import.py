import re

with open("app/src/main/java/com/example/ui/screens/CalculatorsScreen.kt", "r") as f:
    content = f.read()

# Add the import back
import_line = "import androidx.compose.material.icons.automirrored.filled.*"
content = content.replace("import androidx.compose.material.icons.filled.*", "import androidx.compose.material.icons.filled.*\n" + import_line)

with open("app/src/main/java/com/example/ui/screens/CalculatorsScreen.kt", "w") as f:
    f.write(content)
