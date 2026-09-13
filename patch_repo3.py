import re

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if ".set(" in line and ".await()" in line:
        lines[i] = line.replace(".await()", ".addOnFailureListener { e -> Log.e(\"DominoRepository\", \"Write failed\", e) }")
    elif ".delete()" in line and ".await()" in line:
        lines[i] = line.replace(".await()", ".addOnFailureListener { e -> Log.e(\"DominoRepository\", \"Delete failed\", e) }")
    elif ".update(" in line and ".await()" in line:
        lines[i] = line.replace(".await()", ".addOnFailureListener { e -> Log.e(\"DominoRepository\", \"Update failed\", e) }")

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "w") as f:
    f.writelines(lines)

print("Removed all write .await() calls.")
