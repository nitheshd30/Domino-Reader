import re

with open(".github/workflows/build-apk.yml", "r") as f:
    content = f.read()

injection = """          # 1.5 Inject google-services.json for Firebase
          echo "ewogICJwcm9qZWN0X2luZm8iOiB7CiAgICAicHJvamVjdF9udW1iZXIiOiAiMTk1NTY2MjY2NTg0IiwKICAgICJwcm9qZWN0X2lkIjogImRvbWluby1hOTk3OSIsCiAgICAic3RvcmFnZV9idWNrZXQiOiAiZG9taW5vLWE5OTc5LmZpcmViYXNlc3RvcmFnZS5hcHAiCiAgfSwKICAiY2xpZW50IjogWwogICAgewogICAgICAiY2xpZW50X2luZm8iOiB7CiAgICAgICAgIm1vYmlsZXNka19hcHBfaWQiOiAiMToxOTU1NjYyNjY1ODQ6YW5kcm9pZDo1ODJlZTMxN2U2NmViZDQxZWI0ZmFkIiwKICAgICAgICAiYW5kcm9pZF9jbGllbnRfaW5mbyI6IHsKICAgICAgICAgICJwYWNrYWdlX25hbWUiOiAiY29tLmFpc3R1ZGlvLmRvbWlub2F4cmVhZGVyLmtwbXF2eSIKICAgICAgICB9CiAgICAgIH0sCiAgICAgICJvYXV0aF9jbGllbnQiOiBbXSwKICAgICAgImFwaV9rZXkiOiBbCiAgICAgICAgewogICAgICAgICAgImN1cnJlbnRfa2V5IjogIkFJemFTeUFqV0RDNk9KSHhrRUZxZk94SGtLTmtaUk5POS1iZkdqdyIKICAgICAgICB9CiAgICAgIF0sCiAgICAgICJzZXJ2aWNlcyI6IHsKICAgICAgICAiYXBwaW52aXRlX3NlcnZpY2UiOiB7CiAgICAgICAgICAib3RoZXJfcGxhdGZvcm1fb2F1dGhfY2xpZW50IjogW10KICAgICAgICB9CiAgICAgIH0KICAgIH0KICBdLAogICJjb25maWd1cmF0aW9uX3ZlcnNpb24iOiAiMSIKfQ==" | base64 -d > app/google-services.json"""

if "# 2. Ensure .env exists" in content:
    content = content.replace("# 2. Ensure .env exists", injection + "\n          # 2. Ensure .env exists")

with open(".github/workflows/build-apk.yml", "w") as f:
    f.write(content)
