package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab1.ui.theme.Lab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: CourseViewModel by viewModels()

        setContent {
            Lab1Theme {
                CourseApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CourseApp(viewModel: CourseViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }

    // onStart
    LaunchedEffect(Unit) {
        viewModel.loadAllDegreePlans()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ButtonAddNew(onClick = { showDialog = true })
        }
    ) { innerPadding ->
        CourseContent(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding),
            onEditClick = { index ->
                editingIndex = index
                showEditDialog = true
            }
        )
    }

    CourseDialogs(
        showAddDialog = showDialog,
        showEditDialog = showEditDialog,
        editingIndex = editingIndex,
        viewModel = viewModel,
        onAddDialogDismiss = { showDialog = false },
        onEditDialogDismiss = {
            showEditDialog = false
            editingIndex = -1
        }
    )
}

@Composable
fun CourseContent(
    viewModel: CourseViewModel,
    modifier: Modifier = Modifier,
    onEditClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 网络错误提示（可选）
        viewModel.errorMessage?.let { error ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = error, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }

        if (viewModel.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // 课程计划
        item {
            CoursePlanSection(
                courseList = viewModel.courseList,
                onEditClick = onEditClick
            )
        }

        // 要求（下拉选择专业 + 仅显示要求列表）
        item {
            RequirementsSection(viewModel = viewModel)
        }
    }
}

@Composable
fun CoursePlanSection(
    courseList: List<Pair<String, String>>,
    onEditClick: (Int) -> Unit
) {
    Column {
        PaddingTitle(
            name = "Course Plan",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        courseList.forEachIndexed { index, (course, lecturer) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // 你已有的展示函数
                    displayCourse(
                        course = course,
                        lecturer = lecturer
                    )
                }
                ButtonEdit(onClick = { onEditClick(index) })
            }
        }
    }
}

/* -------------------- 下拉选择专业 + 显示要求（不含复选框） -------------------- */

@Composable
fun RequirementsSection(viewModel: CourseViewModel) {
    var major by remember { mutableStateOf<String?>(null) }
    val options = listOf("Computer Science", "English")

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        PaddingTitle(name = "Requirements")

        Spacer(Modifier.height(8.dp))
        Text("Please select your major")

        Spacer(Modifier.height(12.dp))
        MajorDropdown(
            options = options,
            selected = major,
            onSelected = { picked -> major = picked }
        )

        if (major != null) {
            Spacer(Modifier.height(16.dp))
            Text(text = "Requirements for $major")
            Spacer(Modifier.height(8.dp))

            // 只显示所选专业的 requirements
            RequirementListOnly(viewModel = viewModel, major = major!!)
        }
    }
}
@Composable
fun RequirementListOnly(viewModel: CourseViewModel, major: String) {
    val all = viewModel.requiredCourses
    val list = all.filter { it.major == major }

    list.forEach { course ->
        CourseCard(course = course)
    }

    if (list.isEmpty()) {
        Text("No requirements for $major")
    }
}
/* -------------------- 下拉菜单（只读 + 回调） -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MajorDropdown(
    options: List<String>,
    selected: String?,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val text = selected ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {},            // 只读
            readOnly = true,
            label = { Text("Select major") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onSelected(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* -------------------- 弹窗 -------------------- */

@Composable
fun CourseDialogs(
    showAddDialog: Boolean,
    showEditDialog: Boolean,
    editingIndex: Int,
    viewModel: CourseViewModel,
    onAddDialogDismiss: () -> Unit,
    onEditDialogDismiss: () -> Unit
) {
    PopUp(
        visible = showAddDialog,
        onDismiss = onAddDialogDismiss,
        onConfirm = { courseName, lecturer ->
            viewModel.addCourse(courseName, lecturer)
            onAddDialogDismiss()
        }
    )

    if (editingIndex in 0 until viewModel.courseList.size) {
        val current = viewModel.courseList[editingIndex]
        EditPopUp(
            visible = showEditDialog,
            initial1 = current.first,
            initial2 = current.second,
            onDismiss = onEditDialogDismiss,
            onConfirm = { courseName, lecturer ->
                viewModel.updateCourse(editingIndex, courseName, lecturer)
                onEditDialogDismiss()
            }
        )
    }
}