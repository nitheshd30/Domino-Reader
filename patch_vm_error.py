import re

with open("app/src/main/java/com/example/ui/viewmodel/DominoViewModel.kt", "r") as f:
    content = f.read()

old_err = '_importStatusMessage.value = "Import failed: ${e.localizedMessage ?: "Invalid file format"}"'
new_err = '_importStatusMessage.value = "Import failed: ${e.message ?: e.javaClass.simpleName}"'

content = content.replace(old_err, new_err)

with open("app/src/main/java/com/example/ui/viewmodel/DominoViewModel.kt", "w") as f:
    f.write(content)
