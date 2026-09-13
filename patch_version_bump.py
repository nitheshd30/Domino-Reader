import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace("versionCode = 5", "versionCode = 6")
content = content.replace('versionName = "1.4"', 'versionName = "1.5"')

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
