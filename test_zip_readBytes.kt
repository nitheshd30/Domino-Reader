import java.util.zip.ZipInputStream
import java.io.FileInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun main() {
    val f = File("/tmp/zip_test/test2.zip")
    ZipOutputStream(FileOutputStream(f)).use { zos ->
        zos.putNextEntry(ZipEntry("Labels/test.lbl"))
        zos.write("hello".toByteArray())
        zos.closeEntry()
    }
    
    val zis = ZipInputStream(FileInputStream(f))
    var entry = zis.nextEntry
    while(entry != null) {
        println(entry.name)
        try {
            val bytes = zis.readBytes()
            println("Bytes length: " + bytes.size)
        } catch(e: Exception) {
            println("Exception: " + e.message)
        }
        zis.closeEntry()
        entry = zis.nextEntry
    }
}
