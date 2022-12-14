package com.github.pakka_papad.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R
import com.github.pakka_papad.components.OutlinedBox
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.ui.theme.ThemePreference

@Composable
fun SettingsList(
    paddingValues: PaddingValues,
    themePreference: ThemePreference,
    onThemePreferenceChanged: (ThemePreference) -> Unit,
    scanStatus: ScanStatus,
    onScanClicked: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            LookAndFeelSettings(
                themePreference = themePreference,
                onPreferenceChanged = onThemePreferenceChanged,
            )
        }
        item {
            MusicLibrarySettings(
                scanStatus = scanStatus,
                onScanClicked = onScanClicked
            )
        }
        item {
            MadeBy()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LookAndFeelSettings(
    themePreference: ThemePreference,
    onPreferenceChanged: (ThemePreference) -> Unit,
) {
    val spacerModifier = Modifier.height(10.dp)
    OutlinedBox(
        label = "Look and feel",
        contentPadding = PaddingValues(vertical = 13.dp, horizontal = 20.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Material You",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Switch(checked = themePreference.useMaterialYou, onCheckedChange = {
                        onPreferenceChanged(themePreference.copy(useMaterialYou = it))
                    })
                }
                Spacer(spacerModifier)
            }
            var showSelectorDialog by remember { mutableStateOf(false) }
            val buttonText = when (themePreference.theme) {
                UserPreferences.Theme.LIGHT_MODE, UserPreferences.Theme.UNRECOGNIZED -> "Light"
                UserPreferences.Theme.DARK_MODE -> "Dark"
                UserPreferences.Theme.USE_SYSTEM_MODE -> "System"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App theme",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = { showSelectorDialog = true },
                    content = {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
            if (showSelectorDialog) {
                AlertDialog(
                    onDismissRequest = { showSelectorDialog = false },
                    confirmButton = {
                        Button(
                            onClick = { showSelectorDialog = false }
                        ) {
                            Text(
                                text = "OK",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectableGroup()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = (themePreference.theme == UserPreferences.Theme.LIGHT_MODE || themePreference.theme == UserPreferences.Theme.UNRECOGNIZED),
                                    onClick = {
                                        onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.LIGHT_MODE))
                                    }
                                )
                                Text(
                                    text = "Light mode",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = (themePreference.theme == UserPreferences.Theme.DARK_MODE),
                                    onClick = {
                                        onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.DARK_MODE))
                                    }
                                )
                                Text(
                                    text = "Dark mode",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = (themePreference.theme == UserPreferences.Theme.USE_SYSTEM_MODE),
                                    onClick = {
                                        onPreferenceChanged(themePreference.copy(theme = UserPreferences.Theme.USE_SYSTEM_MODE))
                                    }
                                )
                                Text(
                                    text = "System mode",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MusicLibrarySettings(
    scanStatus: ScanStatus,
    onScanClicked: () -> Unit,
) {
    OutlinedBox(
        label = "Music library",
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 13.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        when (scanStatus) {
            is ScanStatus.ScanNotRunning -> {
                Button(
                    onClick = onScanClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Scan for music",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            is ScanStatus.ScanComplete -> {
                Text(
                    text = "Scan Complete",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is ScanStatus.ScanProgress -> {
                var totalSongs by remember { mutableStateOf(0) }
                var scanProgress by remember { mutableStateOf(0f) }
                scanProgress = (scanStatus.parsed.toFloat()) / (scanStatus.total.toFloat())
                totalSongs = scanStatus.total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Found $totalSongs songs",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    CircularProgressIndicator(progress = scanProgress)
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun MadeBy() {
    val githubUrl = "https://github.com/pakka-papad"
    val linkedinUrl = "https://www.linkedin.com/in/sumitzbera/"
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))) {
                    append("Made by ")
                }
                append("Sumit Bera")
            },
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(10.dp))
        val iconModifier = Modifier
            .size(30.dp)
            .alpha(0.5f)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.github_mark),
                contentDescription = "github",
                modifier = iconModifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(githubUrl)
                        context.startActivity(intent)
                    },
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                contentScale = ContentScale.Inside,
            )
            Image(
                painter = painterResource(R.drawable.linkedin),
                contentDescription = "linkedin",
                modifier = iconModifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(linkedinUrl)
                        context.startActivity(intent)
                    },
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                contentScale = ContentScale.Inside,
            )
        }
    }
}