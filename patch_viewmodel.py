import re

with open("app/src/main/java/com/example/ui/viewmodel/DominoViewModel.kt", "r") as f:
    content = f.read()

old_toggle = """    fun toggleTheme() {
        val nextMode = when (_themeMode.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
        setThemeMode(nextMode)
    }"""

new_toggle = """    fun toggleTheme(isCurrentlyDark: Boolean) {
        val nextMode = if (isCurrentlyDark) AppThemeMode.LIGHT else AppThemeMode.DARK
        setThemeMode(nextMode)
    }"""

content = content.replace(old_toggle, new_toggle)

with open("app/src/main/java/com/example/ui/viewmodel/DominoViewModel.kt", "w") as f:
    f.write(content)
