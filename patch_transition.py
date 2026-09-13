import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

old_transition = """                transitionSpec = {
                    androidx.compose.animation.fadeIn() togetherWith androidx.compose.animation.fadeOut()
                }"""

new_transition = """                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (androidx.compose.animation.slideInHorizontally { width -> direction * width } + androidx.compose.animation.fadeIn()) togetherWith 
                    (androidx.compose.animation.slideOutHorizontally { width -> -direction * width } + androidx.compose.animation.fadeOut())
                }"""

content = content.replace(old_transition, new_transition)

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(content)
