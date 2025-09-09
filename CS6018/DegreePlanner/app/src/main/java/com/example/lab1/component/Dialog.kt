// Dialogs.kt
package com.example.lab1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardOptions
@Composable
fun PopUp(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    containerColor: Color = Color(0xFFFDFDFD)
) {
    if (!visible) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            SimplePopUpContent(
                title = "Add New Course",
                initial1 = "",
                initial2 = "",
                onConfirm = onConfirm,
                onDismiss = onDismiss
            )
        }
    }
}


@Composable
fun EditPopUp(
    visible: Boolean,
    initial1: String,
    initial2: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    containerColor: Color = Color(0xFFFDFDFD)
) {
    if (!visible) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            SimplePopUpContent(
                title = "Edit Course",
                initial1 = initial1,
                initial2 = initial2,
                onConfirm = onConfirm,
                onDismiss = onDismiss
            )
        }
    }
}


@Composable
private fun SimplePopUpContent(
    title: String,
    initial1: String,
    initial2: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var field1 by remember { mutableStateOf(initial1) } // 课程名
    var field2 by remember { mutableStateOf(initial2) } // 讲师

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = field1,
            onValueChange = { field1 = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Course Name (e.g. CS6018)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = field2,
            onValueChange = { field2 = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Lecturer (e.g. Ben)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    onConfirm(field1, field2)
                    onDismiss()
                },
                enabled = field1.isNotBlank() && field2.isNotBlank()
            ) { Text("Confirm") }
        }
    }
}