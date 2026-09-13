import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

# Using regex to remove the navigationIcon block
# It starts at navigationIcon = { and ends before title = {
import re
pattern = r"navigationIcon = \{.*?(?=^\s*title = \{)"
new_content = re.sub(pattern, "", content, flags=re.DOTALL | re.MULTILINE)

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(new_content)
