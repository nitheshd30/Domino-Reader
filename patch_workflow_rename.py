import re

with open(".github/workflows/build-apk.yml", "r") as f:
    content = f.read()

old_step = """      - name: Collect Generated APKs
        run: |
          mkdir -p build-apks
          find app/build/outputs/apk/ -name "*.apk" -exec cp {} build-apks/ \\;
          echo "Generated APK files:"
          ls -lh build-apks/"""

new_step = """      - name: Collect Generated APKs
        run: |
          mkdir -p build-apks
          find app/build/outputs/apk/ -name "*.apk" -exec cp {} build-apks/ \\;
          
          # Extract version from build.gradle.kts and rename APKs
          VERSION_NAME=$(grep "versionName =" app/build.gradle.kts | cut -d'"' -f2 || echo "unknown")
          echo "Extracted version: $VERSION_NAME"
          for file in build-apks/*.apk; do
            mv "$file" "${file%.apk}-v${VERSION_NAME}.apk"
          done
          
          echo "Generated APK files:"
          ls -lh build-apks/"""

if old_step in content:
    content = content.replace(old_step, new_step)
    with open(".github/workflows/build-apk.yml", "w") as f:
        f.write(content)
    print("Patched workflow successfully.")
else:
    print("Could not find the Collect Generated APKs step. Please check manually.")

