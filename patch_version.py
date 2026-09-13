import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace("versionCode = 3", "versionCode = 4")
content = content.replace('versionName = "1.2"', 'versionName = "1.3"')

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
