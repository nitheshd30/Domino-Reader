import re

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "r") as f:
    content = f.read()

old_clear = """    suspend fun clearAllData() {
        // Clearing a whole Firestore collection from client is not recommended without a batch loop.
        // We will skip actual clearing for safety on free plan, or just rely on user manual deletion.
    }"""

new_clear = """    suspend fun clearAllData() {
        if (firestore == null) return
        withContext(Dispatchers.IO) {
            val collections = listOf("printer_backups", "domino_labels", "production_logs", "consumable_items", "saved_calculations")
            for (collectionName in collections) {
                try {
                    val snapshot = firestore.collection(collectionName).get().await()
                    if (snapshot.documents.isNotEmpty()) {
                        val batch = firestore.batch()
                        for (document in snapshot.documents) {
                            batch.delete(document.reference)
                        }
                        batch.commit().await()
                    }
                } catch (e: Exception) {
                    Log.e("DominoRepository", "Error clearing $collectionName", e)
                }
            }
        }
    }"""

content = content.replace(old_clear, new_clear)

with open("app/src/main/java/com/example/data/local/DominoRepository.kt", "w") as f:
    f.write(content)
