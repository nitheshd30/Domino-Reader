import re

with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "r") as f:
    content = f.read()

# Add states for edit label dialog
content = content.replace("val inspectingLabel by viewModel.inspectingLabel.collectAsStateWithLifecycle()", 
                          "val inspectingLabel by viewModel.inspectingLabel.collectAsStateWithLifecycle()\n    val showAddEditLabelDialog by viewModel.showAddEditLabelDialog.collectAsStateWithLifecycle()\n    val editingLabel by viewModel.editingLabel.collectAsStateWithLifecycle()")


# Add onEdit
content = content.replace("onUpdateLabel = { viewModel.updateLabel(it) }\n            )", 
                          "onUpdateLabel = { viewModel.updateLabel(it) },\n                onEdit = { viewModel.openEditLabel(label) }\n            )")

# Add the dialog UI
dialog_code = """
        // Add/Edit Label Dialog
        if (showAddEditLabelDialog) {
            AddEditLabelDialog(
                initialLabel = editingLabel,
                printerId = viewModel.selectedPrinterId.collectAsStateWithLifecycle().value ?: 1L,
                onDismiss = { viewModel.closeAddEditLabel() },
                onSave = { label -> viewModel.saveLabel(label) }
            )
        }
"""

content = content.replace("// Label Inspection Dialog", dialog_code + "\n        // Label Inspection Dialog")


with open("app/src/main/java/com/example/ui/screens/DominoMainScreen.kt", "w") as f:
    f.write(content)
