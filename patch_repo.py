import re

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "r") as f:
    content = f.read()

# Replace .await() with .addOnFailureListener to avoid suspending indefinitely on poor network
content = content.replace("firestore.collection(\"printer_backups\").document(newBackupId.toString()).set(finalBackup).await()", "firestore.collection(\"printer_backups\").document(newBackupId.toString()).set(finalBackup).addOnFailureListener { e -> Log.e(\"DominoRepository\", \"Failed to save backup\", e) }")

content = content.replace("batch.commit().await()", "batch.commit().addOnFailureListener { e -> Log.e(\"DominoRepository\", \"Failed to commit batch\", e) }")

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "w") as f:
    f.write(content)

print("Removed await() from Firestore writes to prevent hanging.")
