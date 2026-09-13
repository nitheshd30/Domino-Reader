import re

with open("app/src/main/java/com/example/ui/screens/CalculatorsScreen.kt", "r") as f:
    content = f.read()

pattern = r"            IconButton\(onClick = onBack\) \{\n                Icon\(Icons\.AutoMirrored\.Filled\.ArrowBack, contentDescription = \"Back\"\)\n            \}"
new_content = re.sub(pattern, "", content)

with open("app/src/main/java/com/example/ui/screens/CalculatorsScreen.kt", "w") as f:
    f.write(new_content)
