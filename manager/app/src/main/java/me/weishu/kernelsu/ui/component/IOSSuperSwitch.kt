package me.weishu.kernelsu.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.yukonga.miuix.kmp.basic.BasicComponent

@Composable
fun IOSSuperSwitch(
    title: String,
    summary: String? = null,
    startAction: (@Composable () -> Unit)? = null,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BasicComponent(
        modifier = modifier,
        title = title,
        summary = summary,
        startAction = startAction,
        endActions = {
            IOSSwitch(
                checked = checked,
                onCheckedChange = null // we handle click on the whole row
            )
        },
        onClick = { onCheckedChange?.invoke(!checked) },
        enabled = enabled
    )
}
