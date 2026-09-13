import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

offline_code = """                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Offline Mode",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )"""

cloud_code = """                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cloud Synced",
                                fontSize = 11.sp,
                                color = DominoGreen,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )"""

content = content.replace(offline_code, cloud_code)

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(content)
