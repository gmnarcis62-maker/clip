package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ime.ImeActivationManager
import com.example.domain.ime.KeyboardActivationState
import com.example.ui.theme.PrimaryCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivationWizardBottomSheet(
    currentState: KeyboardActivationState,
    onDismissRequest: () -> Unit,
    onNavigateToTest: () -> Unit,
    onRefreshState: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    // Determine starting step based on actual system state
    var currentStep by remember(currentState) {
        mutableIntStateOf(
            when (currentState) {
                KeyboardActivationState.NOT_ENABLED -> 1
                KeyboardActivationState.ENABLED_NOT_DEFAULT -> 3
                KeyboardActivationState.DEFAULT -> 4
                else -> 1
            }
        )
    }

    var showTroubleshooting by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "راهنمای فعال‌سازی کیبورد Clipbord",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ساده، سریع و در چند ثانیه",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "بستن", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step Indicator Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepDot(stepNumber = 1, isActive = currentStep == 1, isCompleted = currentStep > 1)
                    StepLine(isCompleted = currentStep > 1)
                    StepDot(stepNumber = 2, isActive = currentStep == 2, isCompleted = currentStep > 2)
                    StepLine(isCompleted = currentStep > 2)
                    StepDot(stepNumber = 3, isActive = currentStep == 3, isCompleted = currentStep > 3)
                    StepLine(isCompleted = currentStep > 3)
                    StepDot(stepNumber = 4, isActive = currentStep == 4, isCompleted = currentStep >= 4, isCheck = true)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content Animated per Step
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "step_content"
                ) { step ->
                    when (step) {
                        1 -> StepOneContent(
                            onOpenSettings = {
                                val ok = ImeActivationManager.openInputMethodSettings(context)
                                if (!ok) {
                                    Toast.makeText(context, "امکان باز کردن مستقیم تنظیمات فراهم نشد. لطفاً از تنظیمات دستگاه وارد بخش کیبورد شوید.", Toast.LENGTH_LONG).show()
                                }
                                currentStep = 2
                            },
                            onNext = { currentStep = 2 }
                        )
                        2 -> StepTwoContent(
                            onOpenSettingsAgain = {
                                ImeActivationManager.openInputMethodSettings(context)
                            },
                            onCheckProgress = {
                                onRefreshState()
                                val newState = ImeActivationManager.checkActivationState(context)
                                if (newState == KeyboardActivationState.ENABLED_NOT_DEFAULT) {
                                    currentStep = 3
                                } else if (newState == KeyboardActivationState.DEFAULT) {
                                    currentStep = 4
                                } else {
                                    Toast.makeText(context, "هنوز کلید Clipbord در تنظیمات روشن نشده است.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onNext = { currentStep = 3 }
                        )
                        3 -> StepThreeContent(
                            onSelectKeyboard = {
                                val ok = ImeActivationManager.showInputMethodPicker(context)
                                if (!ok) {
                                    Toast.makeText(context, "منوی انتخاب کیبورد باز نشد.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onVerifySelection = {
                                onRefreshState()
                                val newState = ImeActivationManager.checkActivationState(context)
                                if (newState == KeyboardActivationState.DEFAULT) {
                                    currentStep = 4
                                } else {
                                    Toast.makeText(context, "لطفاً از پنجره باز شده، Clipbord را انتخاب کنید.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        4 -> StepFourSuccessContent(
                            onNavigateToTest = {
                                onDismissRequest()
                                onNavigateToTest()
                            },
                            onDone = onDismissRequest
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Troubleshooting Toggle Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTroubleshooting = !showTroubleshooting }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "کیبورد فعال نشد؟ راهنمای عیب‌یابی",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = if (showTroubleshooting) "بستن ▲" else "مشاهده ▼",
                                fontSize = 11.sp,
                                color = PrimaryCyan
                            )
                        }

                        if (showTroubleshooting) {
                            Spacer(modifier = Modifier.height(10.dp))
                            TroubleshootingChecklist(
                                onOpenSettings = { ImeActivationManager.openInputMethodSettings(context) },
                                onShowPicker = { ImeActivationManager.showInputMethodPicker(context) },
                                onRefresh = {
                                    onRefreshState()
                                    Toast.makeText(context, "وضعیت بررسی شد.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                // General device compatibility note
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "💡 نکته: ظاهر صفحه تنظیمات در گوشی‌های سامسونگ، شیائومی، هواوی، پیکسل و... ممکن است اندکی متفاوت باشد، اما نام Clipbord در لیست کیبوردهای مدیریت‌شده قرار دارد.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// === Sub-components for Wizard Steps ===

@Composable
private fun StepOneContent(
    onOpenSettings: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Educational Mockup
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(PrimaryCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "مرحله ۱ از ۳: ورود به تنظیمات کیبوردها",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "با لمس دکمه زیر، صفحه رسمی «مدیریت کیبوردها» در تنظیمات اندروید باز می‌شود.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_wizard_open_settings")
        ) {
            Icon(Icons.Default.OpenInNew, null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("۱. باز کردن تنظیمات کیبورد اندروید", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            Text("قبلاً باز کرده‌ام، مرحله بعد →", fontSize = 12.sp)
        }
    }
}

@Composable
private fun StepTwoContent(
    onOpenSettingsAgain: () -> Unit,
    onCheckProgress: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Educational Mockup of the Toggle Switch
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "مرحله ۲ از ۳: روشن کردن کلید Clipbord",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Simulated Android Settings Row
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, PrimaryCyan)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Keyboard, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("مرسانا | Mersana", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("کیبورد هوشمند فارسی", fontSize = 10.sp, color = PrimaryCyan)
                            }
                        }

                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = PrimaryCyan
                            ),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Security Note Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFB800).copy(alpha = 0.12f),
                    border = BorderStroke(0.5.dp, Color(0xFFFFB800).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "هشدار امنیتی اندروید: سیستم‌عامل اندروید هنگام فعال‌سازی هر کیبورد جدیدی یک پیام استاندارد نمایش می‌دهد. کیبورد Clipbord حریم خصوصی شما را کاملاً رعایت کرده و اطلاعات شما امن است.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCheckProgress,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_wizard_verify_enabled")
        ) {
            Icon(Icons.Default.Refresh, null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("فعال کردم، بررسی و ادامه", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onOpenSettingsAgain,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(38.dp)
            ) {
                Text("باز کردن مجدد تنظیمات", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = onNext,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(38.dp)
            ) {
                Text("مرحله ۳ →", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun StepThreeContent(
    onSelectKeyboard: () -> Unit,
    onVerifySelection: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "مرحله ۳ از ۳: انتخاب مرسانا به عنوان کیبورد اصلی",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "روی دکمه زیر بزنید و در پنجره ظاهر شده، گزینه «مرسانا | Mersana» را لمس کنید تا فعال شود.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSelectKeyboard,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_wizard_open_picker")
        ) {
            Icon(Icons.Default.TouchApp, null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("انتخاب کیبورد مرسانا", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onVerifySelection,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(42.dp)
        ) {
            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("انتخاب کردم، بررسی وضعیت", fontSize = 12.sp)
        }
    }
}

@Composable
private fun StepFourSuccessContent(
    onNavigateToTest: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF10B981).copy(alpha = 0.15f),
            border = BorderStroke(1.5.dp, Color(0xFF10B981)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "🎉 تبریک! مرسانا آماده استفاده است",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "کیبورد مخملی مرسانا اکنون کیبورد اصلی گوشی شماست و در تمام برنامه‌ها (تلگرام، ایتا، روبیکا، واتساپ، پیامک و...) فعال است.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToTest,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_wizard_go_to_test")
        ) {
            Icon(Icons.Default.Keyboard, null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("آزمایش و تست کیبورد", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onDone,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(42.dp)
        ) {
            Text("متوجه شدم، رفتن به صفحه اصلی", fontSize = 12.sp)
        }
    }
}

@Composable
private fun TroubleshootingChecklist(
    onOpenSettings: () -> Unit,
    onShowPicker: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TroubleshootItem("۱. مطمئن شوید در تنظیمات اندروید، کلید کنار «مرسانا» روشن (فعال) شده است.")
        TroubleshootItem("۲. در منوی انتخاب کیبورد (Input Method Picker)، گزینه مرسانا را انتخاب کنید.")
        TroubleshootItem("۳. در برخی گوشی‌های شیائومی/پوکو، پس از فعال‌سازی باید پیام تایید امنیتی MIUI را تایید کنید.")
        TroubleshootItem("۴. اگر همچنان کیبورد بالا نمی‌آید، یک بار برنامه را ببندید و دوباره باز کنید.")

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(34.dp)
            ) {
                Text("تنظیمات کیبورد", fontSize = 10.sp)
            }

            Button(
                onClick = onShowPicker,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(34.dp)
            ) {
                Text("انتخاب کیبورد", fontSize = 10.sp)
            }

            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TroubleshootItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("•", color = PrimaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun StepDot(
    stepNumber: Int,
    isActive: Boolean,
    isCompleted: Boolean,
    isCheck: Boolean = false
) {
    val bgColor = when {
        isCompleted -> Color(0xFF10B981)
        isActive -> PrimaryCyan
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        isCompleted || isActive -> Color.Black
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(
                1.dp,
                if (isActive) PrimaryCyan else Color.Transparent,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted || isCheck && isCompleted) {
            Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(16.dp))
        } else {
            Text(
                text = stepNumber.toString(),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StepLine(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .width(28.dp)
            .height(2.dp)
            .background(if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant)
    )
}
