import re

with open("app/src/main/java/com/example/ui/viewmodel/DominoViewModel.kt", "r") as f:
    content = f.read()

# Let's print the actual stacktrace to logcat
old_try = """            } catch (e: Exception) {"""
new_try = """            } catch (e: Exception) {
                android.util.Log.e("ImportError", "Failed to import", e)"""

content = content.replace(old_try, new_try)

with open("app/src/main/java/com/example/ui/viewmodel/DominoViewModel.kt", "w") as f:
    f.write(content)
