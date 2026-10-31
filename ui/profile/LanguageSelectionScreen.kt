package com.siamts.synapse.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siamts.synapse.ui.theme.BlueAccent
import com.siamts.synapse.ui.theme.DarkBackgroundBottom
import com.siamts.synapse.ui.theme.DarkBackgroundTop
import com.siamts.synapse.ui.theme.SYNAPSETheme
import com.siamts.synapse.ui.theme.TextMuted
import com.siamts.synapse.ui.theme.TextPrimary
import com.siamts.synapse.ui.welcome.components.DashedBorderBox

@Composable
fun LanguageSelectionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialLanguage: String = "English",
    onLanguageSelected: (String) -> Unit = {},
) {
    var selectedLanguage by remember { mutableStateOf(initialLanguage) }

    val languages = listOf(
        Triple("English", "🇺🇸", "English"),
        Triple("हिन्दी", "🇮🇳", "Hindi"),
        Triple("தமிழ்", "🇮🇳", "Tamil"),
        Triple("తెలుగు", "🇮🇳", "Telugu"),
        Triple("ಕನ್ನಡ", "🇮🇳", "Kannada"),
        Triple("മലയാളം", "🇮🇳", "Malayalam"),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackgroundTop,
                        Color(0xFF090D22),
                        DarkBackgroundBottom,
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Box
            DashedBorderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBackClick) {
                        Text(text = "←", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(text = "PROFILE & SETTINGS", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Language", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Light Sheet Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashedBorderBox(modifier = Modifier.fillMaxWidth(), contentPadding = 8.dp) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    languages.forEach { (langName, flag, code) ->
                                        val isSel = selectedLanguage.equals(code, ignoreCase = true)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedLanguage = code
                                                    onLanguageSelected(code)
                                                }
                                                .background(if (isSel) Color(0xFFEFF6FF) else Color.Transparent)
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = flag, fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(text = langName, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium)
                                            }

                                            RadioButton(
                                                selected = isSel,
                                                onClick = {
                                                    selectedLanguage = code
                                                    onLanguageSelected(code)
                                                },
                                                colors = RadioButtonDefaults.colors(selectedColor = BlueAccent)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LanguageSelectionScreenPreview() {
    SYNAPSETheme {
        LanguageSelectionScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
    }
}
