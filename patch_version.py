import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace("versionCode = 6", "versionCode = 7")
content = content.replace('versionName = "1.5"', 'versionName = "1.6"')

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
