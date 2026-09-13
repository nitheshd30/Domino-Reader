import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

# The DropdownMenuItem block to remove:
#                            DropdownMenuItem(
#                                text = { Text("Restore Sample Data") },
#                                leadingIcon = {
#                                    Icon(
#                                        imageVector = Icons.Default.Refresh,
#                                        contentDescription = null,
#                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
#                                    )
#                                },
#                                onClick = {
#                                    moreMenuExpanded = false
#                                    viewModel.resetToDefaults()
#                                }
#                            )
#                            HorizontalDivider()

pattern = r"                            DropdownMenuItem\(\s*text = \{ Text\(\"Restore Sample Data\"\) \},\s*leadingIcon = \{\s*Icon\(\s*imageVector = Icons\.Default\.Refresh,\s*contentDescription = null,\s*tint = MaterialTheme\.colorScheme\.onSurfaceVariant\s*\)\s*\},\s*onClick = \{\s*moreMenuExpanded = false\s*viewModel\.resetToDefaults\(\)\s*\}\s*\)\s*HorizontalDivider\(\)"

new_content = re.sub(pattern, "", content)

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(new_content)
