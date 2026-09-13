import re

def fix_file(filepath):
    with open(filepath, "r") as f:
        content = f.read()
    
    old_init = "FirebaseApp.initializeApp(context)"
    new_init = """if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(context)
            }"""
            
    content = content.replace(old_init, new_init)
    
    with open(filepath, "w") as f:
        f.write(content)

fix_file("app/src/main/java/com/example/data/local/DominoRepository.kt")
fix_file("app/src/main/java/com/example/ui/screens/OvertimePrefs.kt")
