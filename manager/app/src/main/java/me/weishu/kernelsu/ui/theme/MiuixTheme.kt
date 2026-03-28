package me.weishu.kernelsu.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import com.materialkolor.dynamiccolor.ColorSpec
import me.weishu.kernelsu.ui.webui.MonetColorsProvider
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeColorSpec
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle

@Composable
fun MiuixKernelSUTheme(
    appSettings: AppSettings,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = appSettings.colorMode.isDark || (appSettings.colorMode.isSystem && systemDarkTheme)
    val colorStyle = appSettings.paletteStyle
    val colorSpec = appSettings.colorSpec

    val miuixPaletteStyle = try {
        ThemePaletteStyle.valueOf(colorStyle.name)
    } catch (_: Exception) {
        ThemePaletteStyle.TonalSpot
    }

    val miuixColorSpec = if (colorSpec == ColorSpec.SpecVersion.SPEC_2025) {
        ThemeColorSpec.Spec2025
    } else {
        ThemeColorSpec.Spec2021
    }

    val controller = ThemeController(
        when (appSettings.colorMode) {
            ColorMode.SYSTEM -> ColorSchemeMode.System
            ColorMode.LIGHT -> ColorSchemeMode.Light
            ColorMode.DARK -> ColorSchemeMode.Dark
            ColorMode.MONET_SYSTEM -> ColorSchemeMode.MonetSystem
            ColorMode.MONET_LIGHT -> ColorSchemeMode.MonetLight
            ColorMode.MONET_DARK, ColorMode.DARK_AMOLED -> ColorSchemeMode.MonetDark
        },
        keyColor = if (appSettings.keyColor == 0) null else Color(appSettings.keyColor),
        isDark = darkTheme,
        paletteStyle = miuixPaletteStyle,
        colorSpec = miuixColorSpec,
    )

    MiuixTheme(
        controller = controller,
        content = {
            // iOS 26 Liquid Glass: deeper transparency + subtle tint layering
            val glassAlpha = if (darkTheme) 0.20f else 0.25f
            val containerTint = if (darkTheme) 0.15f else 0.18f
            val transparentColors = MiuixTheme.colorScheme.copy(
                surface = MiuixTheme.colorScheme.surface.copy(alpha = glassAlpha),
                surfaceContainer = MiuixTheme.colorScheme.surfaceContainer.copy(alpha = containerTint),
                surfaceContainerHigh = MiuixTheme.colorScheme.surfaceContainerHigh.copy(alpha = containerTint),
                surfaceContainerHighest = MiuixTheme.colorScheme.surfaceContainerHighest.copy(alpha = glassAlpha)
            )
            MiuixTheme(colors = transparentColors) {
                LaunchedEffect(darkTheme) {
                    val window = (context as? Activity)?.window ?: return@LaunchedEffect
                    WindowInsetsControllerCompat(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                }
                MonetColorsProvider.UpdateCss()
                CompositionLocalProvider(
                    LocalContentColor provides MiuixTheme.colorScheme.onBackground,
                ) {
                    content()
                }
            }
        }
    )
}
