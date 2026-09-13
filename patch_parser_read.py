import re

with open("app/src/main/java/com/example/data/local/DominoBackupParser.kt", "r") as f:
    content = f.read()

# Replace zip.readBytes() with a safe way to read entry bytes without closing the stream
old_read = "val entryBytes = zip.readBytes()"
new_read = """val buffer = java.io.ByteArrayOutputStream()
                    val chunk = ByteArray(1024)
                    var read = zip.read(chunk)
                    while (read != -1) {
                        buffer.write(chunk, 0, read)
                        read = zip.read(chunk)
                    }
                    val entryBytes = buffer.toByteArray()"""

content = content.replace(old_read, new_read)

with open("app/src/main/java/com/example/data/local/DominoBackupParser.kt", "w") as f:
    f.write(content)
