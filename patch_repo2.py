import re

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "r") as f:
    content = f.read()

old_await = 'firestore.collection("printer_backups").document(newBackupId.toString()).set(tempBackup).await()'
new_await = 'firestore.collection("printer_backups").document(newBackupId.toString()).set(tempBackup).addOnFailureListener { e -> Log.e("DominoRepository", "Failed to save temp backup", e) }'

content = content.replace(old_await, new_await)

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "w") as f:
    f.write(content)

print("Patched temp backup await().")
