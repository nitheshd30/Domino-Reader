import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace("versionCode = 1", "versionCode = 2")
content = content.replace('versionName = "1.0"', 'versionName = "1.1"')

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
