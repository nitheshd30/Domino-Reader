import re

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "r") as f:
    content = f.read()

old_loop = """            // Insert labels in batches if many
            parsed.labels.forEachIndexed { index, label ->
                val id = System.currentTimeMillis() + index
                firestore.collection("domino_labels").document(id.toString()).set(label.copy(id = id, printerBackupId = newBackupId)).await()
            }
            
            parsed.logs.forEachIndexed { index, log ->
                val id = System.currentTimeMillis() + index
                firestore.collection("production_logs").document(id.toString()).set(log.copy(id = id, printerBackupId = newBackupId)).await()
            }"""

new_loop = """            // Insert labels in batches of 500
            val labelChunks = parsed.labels.chunked(500)
            labelChunks.forEachIndexed { chunkIndex, chunk ->
                val batch = firestore.batch()
                chunk.forEachIndexed { index, label ->
                    val id = System.currentTimeMillis() + (chunkIndex * 500) + index
                    val docRef = firestore.collection("domino_labels").document(id.toString())
                    batch.set(docRef, label.copy(id = id, printerBackupId = newBackupId))
                }
                batch.commit().await()
            }
            
            val logChunks = parsed.logs.chunked(500)
            logChunks.forEachIndexed { chunkIndex, chunk ->
                val batch = firestore.batch()
                chunk.forEachIndexed { index, log ->
                    val id = System.currentTimeMillis() + (chunkIndex * 500) + index
                    val docRef = firestore.collection("production_logs").document(id.toString())
                    batch.set(docRef, log.copy(id = id, printerBackupId = newBackupId))
                }
                batch.commit().await()
            }"""

if old_loop in content:
    content = content.replace(old_loop, new_loop)
    with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "w") as f:
        f.write(content)
    print("Patched batch upload.")
else:
    print("Could not find the loop.")

