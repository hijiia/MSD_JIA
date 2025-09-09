package com.example.lab1

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1.ui.theme.PurpleUI

@Composable
fun ButtonAddNew(onClick: () -> Unit) {
    BottomButton(onClick = onClick) {
        Text("Add new class")
    }
}

@Composable
fun ButtonEdit(onClick: () -> Unit) {
    BottomButton(onClick = onClick) {
        Text("Edit")
    }
}

@Composable
fun ButtonAddRequirements(onClick: () -> Unit) {
    BottomButton(onClick = onClick) {
        Text("Add requirements")
    }
}

@Composable
fun BottomButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled
    ) {
        content()
    }
}

// Preview components
@Preview(showBackground = true)
@Composable
fun ButtonAddNewPreview() {
    BottomButton(
        onClick = { println("Button clicked in Preview") }
    ) {
        Text("Add new class")
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonEditPreview() {
    BottomButton(
        onClick = { println("Button clicked in Preview") }
    ) {
        Text("Edit")
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonAddRqPreview() {
    BottomButton(
        onClick = { println("Button clicked in Preview") }
    ) {
        Text("Add requirements")
    }
}