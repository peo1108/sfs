package me.weishu.kernelsu.ui.screen.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ContactPage
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.ElectricalServices
import androidx.compose.material.icons.rounded.Fence
import androidx.compose.material.icons.rounded.FolderDelete
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material.icons.rounded.RemoveModerator
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import me.weishu.kernelsu.ui.util.rememberGyroGlowBrush
import me.weishu.kernelsu.ui.util.rememberGyroRadialGlow
import me.weishu.kernelsu.ui.util.doubleBezelCard
import me.weishu.kernelsu.ui.util.pressScale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeSource
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.UiMode
import me.weishu.kernelsu.ui.component.KsuIsValid
import me.weishu.kernelsu.ui.component.dialog.rememberLoadingDialog
import me.weishu.kernelsu.ui.component.miuix.SendLogDialog
import me.weishu.kernelsu.ui.component.uninstalldialog.UninstallDialog
import me.weishu.kernelsu.ui.theme.LocalEnableBlur
import me.weishu.kernelsu.ui.util.defaultHazeEffect
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import me.weishu.kernelsu.ui.component.IOSSuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * @author weishu
 * @date 2023/1/1.
 */
@Composable
fun SettingPagerMiuix(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp
) {
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val hazeState = remember { HazeState() }
    val hazeStyle = if (enableBlur) {
        HazeStyle(
            backgroundColor = Color.Transparent,
            tint = HazeTint(colorScheme.surface.copy(0.05f)),
            blurRadius = 25.dp // iOS 26: deep blur
        )
    } else {
        HazeStyle.Unspecified
    }
    val loadingDialog = rememberLoadingDialog()
    val showUninstallDialog = rememberSaveable { mutableStateOf(false) }
    val showSendLogDialog = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = if (enableBlur) {
                    Modifier.defaultHazeEffect(hazeState, hazeStyle)
                } else {
                    Modifier
                },
                color = Color.Transparent,
                title = stringResource(R.string.settings),
                scrollBehavior = scrollBehavior
            )
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        // iOS 26: Settings Card glow border with gyro
        val miniCardShape = com.kyant.capsule.ContinuousRoundedRectangle(16.dp)
        val tiltValue = me.weishu.kernelsu.ui.util.LocalGyroTilt.current
        val miniGlowBrush = rememberGyroGlowBrush(
            primaryColor = Color.White,
            accentColor = colorScheme.primary,
            tilt = tiltValue
        )
        val radialGlow = rememberGyroRadialGlow(tilt = tiltValue)

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .let { if (enableBlur) it.hazeSource(state = hazeState) else it }
                .padding(horizontal = 16.dp), // iOS 26: wider inset margins
            contentPadding = innerPadding,
                    ) {
            item {
                Card(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .pressScale()
                        .doubleBezelCard(
                            shape = miniCardShape,
                            glowBrush = miniGlowBrush,
                            radialGlow = radialGlow,
                        ),
                    colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f))
                ) {
                    Column {
                        IOSSuperSwitch(
                            title = stringResource(id = R.string.settings_check_update),
                            summary = stringResource(id = R.string.settings_check_update_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Update,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_check_update),
                                    tint = colorScheme.onBackground
                                )
                            },
                            checked = uiState.checkUpdate,
                            onCheckedChange = actions.onSetCheckUpdate
                        )
                        KsuIsValid {
                            IOSSuperSwitch(
                                title = stringResource(id = R.string.settings_module_check_update),
                                summary = stringResource(id = R.string.settings_check_update_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.UploadFile,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(id = R.string.settings_check_update),
                                        tint = colorScheme.onBackground
                                    )
                                },
                                checked = uiState.checkModuleUpdate,
                                onCheckedChange = actions.onSetCheckModuleUpdate
                            )
                        }
                    }
                }

                Card(colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f)), // iOS 26
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .pressScale()
                        .doubleBezelCard(
                            shape = miniCardShape,
                            glowBrush = miniGlowBrush,
                            radialGlow = radialGlow,
                        ),
                ) {
                    SuperDropdown(
                        title = stringResource(id = R.string.settings_ui_mode),
                        summary = stringResource(id = R.string.settings_ui_mode_summary),
                        items = UiMode.entries.map { it.name },
                        startAction = {
                            Icon(
                                Icons.Rounded.Dashboard,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = stringResource(id = R.string.settings_ui_mode),
                                tint = colorScheme.onBackground
                            )
                        },
                        selectedIndex = if (uiState.uiMode == UiMode.Material.value) 1 else 0,
                        onSelectedIndexChange = actions.onSetUiModeIndex
                    )
                    SuperArrow(
                        title = stringResource(id = R.string.settings_theme),
                        summary = stringResource(id = R.string.settings_theme_summary),
                        startAction = {
                            Icon(
                                Icons.Rounded.Palette,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = stringResource(id = R.string.settings_theme),
                                tint = colorScheme.onBackground
                            )
                        },
                        onClick = actions.onOpenTheme
                    )
                }

                KsuIsValid {
                    Card(colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f)), // iOS 26
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .pressScale()
                            .doubleBezelCard(
                                shape = miniCardShape,
                                glowBrush = miniGlowBrush,
                                radialGlow = radialGlow,
                            ),
                    ) {
                        val profileTemplate = stringResource(id = R.string.settings_profile_template)
                        SuperArrow(
                            title = profileTemplate,
                            summary = stringResource(id = R.string.settings_profile_template_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Fence,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = profileTemplate,
                                    tint = colorScheme.onBackground
                                )
                            },
                            onClick = actions.onOpenProfileTemplate
                        )
                    }
                }

                KsuIsValid {
                    Card(colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f)), // iOS 26
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .pressScale()
                            .doubleBezelCard(
                                shape = miniCardShape,
                                glowBrush = miniGlowBrush,
                                radialGlow = radialGlow,
                            ),
                    ) {
                        val suCompatModeItems = listOf(
                            stringResource(id = R.string.settings_mode_enable_by_default),
                            stringResource(id = R.string.settings_mode_disable_until_reboot),
                            stringResource(id = R.string.settings_mode_disable_always),
                        )

                        val suSummary = when (uiState.suCompatStatus) {
                            "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                            "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                            else -> stringResource(id = R.string.settings_sucompat_summary)
                        }
                        SuperDropdown(
                            title = stringResource(id = R.string.settings_sucompat),
                            summary = suSummary,
                            items = suCompatModeItems,
                            startAction = {
                                Icon(
                                    Icons.Rounded.RemoveModerator,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_sucompat),
                                    tint = colorScheme.onBackground
                                )
                            },
                            enabled = uiState.suCompatStatus == "supported",
                            selectedIndex = uiState.suCompatMode,
                            onSelectedIndexChange = actions.onSetSuCompatMode
                        )

                        val umountSummary = when (uiState.kernelUmountStatus) {
                            "unsupported" -> stringResource(id = R.string.feature_status_unsupported_summary)
                            "managed" -> stringResource(id = R.string.feature_status_managed_summary)
                            else -> stringResource(id = R.string.settings_kernel_umount_summary)
                        }
                        IOSSuperSwitch(
                            title = stringResource(id = R.string.settings_kernel_umount),
                            summary = umountSummary,
                            startAction = {
                                Icon(
                                    Icons.Rounded.RemoveCircle,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_kernel_umount),
                                    tint = colorScheme.onBackground
                                )
                            },
                            enabled = uiState.kernelUmountStatus == "supported",
                            checked = uiState.isKernelUmountEnabled,
                            onCheckedChange = actions.onSetKernelUmountEnabled
                        )

                        IOSSuperSwitch(
                            title = stringResource(id = R.string.settings_umount_modules_default),
                            summary = stringResource(id = R.string.settings_umount_modules_default_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.FolderDelete,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_umount_modules_default),
                                    tint = colorScheme.onBackground
                                )
                            },
                            checked = uiState.isDefaultUmountModules,
                            onCheckedChange = actions.onSetDefaultUmountModules
                        )

                        IOSSuperSwitch(
                            title = stringResource(id = R.string.enable_web_debugging),
                            summary = stringResource(id = R.string.enable_web_debugging_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.DeveloperMode,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.enable_web_debugging),
                                    tint = colorScheme.onBackground
                                )
                            },
                            checked = uiState.enableWebDebugging,
                            onCheckedChange = actions.onSetEnableWebDebugging
                        )
                        IOSSuperSwitch(
                            title = stringResource(id = R.string.settings_auto_jailbreak),
                            summary = stringResource(id = R.string.settings_auto_jailbreak_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.ElectricalServices,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(id = R.string.settings_auto_jailbreak),
                                    tint = if (uiState.isLateLoadMode) colorScheme.onSurfaceVariantSummary else colorScheme.disabledOnSecondaryVariant
                                )
                            },
                            enabled = uiState.isLateLoadMode,
                            checked = uiState.autoJailbreak,
                            onCheckedChange = actions.onSetAutoJailbreak
                        )
                    }
                }

                if (uiState.isLkmMode) {
                    Card(colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f)), // iOS 26
                        modifier = Modifier
                            .padding(top = 16.dp) // iOS 26
                            .fillMaxWidth()
                            .shadow(
                                elevation = 6.dp,
                                shape = miniCardShape,
                                ambientColor = colorScheme.primary.copy(alpha = 0.15f),
                                spotColor = colorScheme.primary.copy(alpha = 0.10f)
                            )
                            .clip(miniCardShape)
                            .border(
                                width = 0.5.dp,
                                brush = miniGlowBrush,
                                shape = miniCardShape
                            ),
                    ) {
                        val uninstall = stringResource(id = R.string.settings_uninstall)
                        SuperArrow(
                            title = uninstall,
                            enabled = !uiState.isLateLoadMode,
                            startAction = {
                                Icon(
                                    Icons.Rounded.Delete,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = uninstall,
                                    tint = colorScheme.onBackground,
                                )
                            },
                            onClick = { showUninstallDialog.value = true }
                        )
                        UninstallDialog(
                            show = showUninstallDialog.value,
                            onDismissRequest = { showUninstallDialog.value = false }
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .pressScale()
                        .doubleBezelCard(
                            shape = miniCardShape,
                            glowBrush = miniGlowBrush,
                            radialGlow = radialGlow,
                        ),
                    colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f))
                ) {
                    SuperArrow(
                        title = stringResource(id = R.string.send_log),
                        startAction = {
                            Icon(
                                Icons.Rounded.BugReport,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = stringResource(id = R.string.send_log),
                                tint = colorScheme.onBackground
                            )
                        },
                        onClick = { showSendLogDialog.value = true },
                    )
                    SendLogDialog(
                        show = showSendLogDialog.value,
                        onDismissRequest = { showSendLogDialog.value = false },
                        loadingDialog = loadingDialog
                    )
                    val about = stringResource(id = R.string.about)
                    SuperArrow(
                        title = about,
                        startAction = {
                            Icon(
                                Icons.Rounded.ContactPage,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = about,
                                tint = colorScheme.onBackground
                            )
                        },
                        onClick = actions.onOpenAbout
                    )
                }
                Spacer(Modifier.height(bottomInnerPadding))
            }
        }
    }
}
