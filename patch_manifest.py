import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

perm = '    <uses-permission android:name="android.permission.INTERNET" />\n'
if "android.permission.INTERNET" not in content:
    content = content.replace("<application", perm + "    <application")

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
