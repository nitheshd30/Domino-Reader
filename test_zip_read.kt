import java.util.zip.ZipInputStream
import java.io.FileInputStream
import java.io.ByteArrayOutputStream

fun main() {
    try {
        val zis = ZipInputStream(FileInputStream("/tmp/zip_test/dummy.zip"))
        var entry = zis.nextEntry
        while(entry != null) {
            println("Entry: ${entry.name}")
            val bytes = zis.readBytes()
            println("Read ${bytes.size} bytes")
            zis.closeEntry()
            entry = zis.nextEntry
        }
        zis.close()
    } catch(e: Exception) {
        println("Error: ${e.message}")
    }
}
