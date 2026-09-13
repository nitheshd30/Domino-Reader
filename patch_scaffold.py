import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

content = content.replace("""        floatingActionButton = {
            if (currentTab == 0) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { viewModel.openAddLabel() },
                    containerColor = com.example.ui.theme.DominoCyan
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add Label", tint = androidx.compose.ui.graphics.Color.Black)
                }
            } else if (currentTab == 3) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { viewModel.openAddConsumable() },
                    containerColor = com.example.ui.theme.DominoCyan
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add Consumable", tint = androidx.compose.ui.graphics.Color.Black)
                }
            }
        },""", """        floatingActionButton = {
            if (currentTab == 0) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { viewModel.openAddLabel() },
                    containerColor = com.example.ui.theme.DominoCyan
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add Label", tint = androidx.compose.ui.graphics.Color.Black)
                }
            }
        },""")

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(content)
