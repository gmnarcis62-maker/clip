package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.shamsi.PersianDateUtils
import com.example.ui.components.ClipbordTopBar
import com.example.ui.components.PersianCard
import com.example.ui.components.SectionTitle
import com.example.ui.theme.PrimaryCyan

@Composable
fun KeyboardTestScreen(
    onBackClick: () -> Unit
) {
    var generalText by remember { mutableStateOf("") }
    var numbersText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var multiLineText by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "محیط آزمایش کیبورد",
                    subtitle = "بررسی تایپ فارسی، نیم‌فاصله، اعداد و کاراکترها",
                    onBackClick = onBackClick
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .testTag("keyboard_test_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Banner & Quick Test Chips
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryCyan.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "در فیلدهای زیر تایپ کنید یا نمونه‌های آماده را لمس کنید تا کیبورد Clipbord را تست نمایید:",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Test Preset Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { generalText = "من می‌روم و کتاب‌هایم را می‌خوانم." },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("تست نیم‌فاصله", color = PrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { numbersText = "۱۲۵۰۰۰" },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("تست عدد فارسی", color = PrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { generalText = "مسئولیت و پشتکار" },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan.copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(32.dp)
                                ) {
                                    Text("تست دیکشنری", color = PrimaryCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 1. General Persian Text
                item {
                    PersianCard {
                        SectionTitle(title = "۱. تایپ متنی فارسی و انگلیسی", icon = Icons.Default.Keyboard)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = generalText,
                            onValueChange = { generalText = it },
                            placeholder = { Text("مثال: سلام، حالتون چطوره؟ می‌خوام سفارش بدم...", fontSize = 13.sp) },
                            trailingIcon = {
                                if (generalText.isNotEmpty()) {
                                    IconButton(onClick = { generalText = "" }) {
                                        Icon(Icons.Default.Clear, "پاک کردن")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("test_input_general"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        if (generalText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تعداد حروف: ${PersianDateUtils.toPersianDigits(generalText.length)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. Numbers and Digits Field
                item {
                    PersianCard {
                        SectionTitle(title = "۲. فیلد شماره و اعداد (۰ تا ۹)", icon = Icons.Default.Keyboard)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = numbersText,
                            onValueChange = { numbersText = it },
                            placeholder = { Text("مثال: ۰۹۱۲۳۴۵۶۷۸۹ یا ۱۲۳۴۵", fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                if (numbersText.isNotEmpty()) {
                                    IconButton(onClick = { numbersText = "" }) {
                                        Icon(Icons.Default.Clear, "پاک کردن")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("test_input_numbers"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                // 3. Password / Sensitive Field (Auto-correct disabled for security)
                item {
                    PersianCard {
                        SectionTitle(title = "۳. فیلد رمز عبور (عدم ذخیره در پیش‌بینی)", icon = Icons.Default.Keyboard)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = passwordText,
                            onValueChange = { passwordText = it },
                            placeholder = { Text("رمز عبور را وارد کنید...", fontSize = 13.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                if (passwordText.isNotEmpty()) {
                                    IconButton(onClick = { passwordText = "" }) {
                                        Icon(Icons.Default.Clear, "پاک کردن")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("test_input_password"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                // 4. Multi-line Text Area
                item {
                    PersianCard {
                        SectionTitle(title = "۴. یادداشت چندخطی و پاراگراف", icon = Icons.Default.Keyboard)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = multiLineText,
                            onValueChange = { multiLineText = it },
                            placeholder = { Text("متن طولانی خود را تایپ نمایید...", fontSize = 13.sp) },
                            minLines = 3,
                            maxLines = 6,
                            modifier = Modifier.fillMaxWidth().testTag("test_input_multiline"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                // Clear All Button
                item {
                    Button(
                        onClick = {
                            generalText = ""
                            numbersText = ""
                            passwordText = ""
                            multiLineText = ""
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("پاکسازی تمام فیلدها", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
