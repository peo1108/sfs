package me.weishu.kernelsu.ui.screen.executemoduleaction

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.R
import me.weishu.kernelsu.data.repository.ModuleRepositoryImpl
import me.weishu.kernelsu.ui.util.runModuleAction
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExecuteModuleActionEffect(
    moduleId: String,
    text: String,
    logContent: StringBuilder,
    fromShortcut: Boolean,
    onTextUpdate: (String) -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val noModule = stringResource(R.string.no_such_module)
    val moduleUnavailable = stringResource(R.string.module_unavailable)
    val moduleActionSuccess = stringResource(R.string.module_action_success)

    LaunchedEffect(Unit) {
        if (text.isNotEmpty()) {
            return@LaunchedEffect
        }
        val repo = ModuleRepositoryImpl()
        val modules = repo.getModules().getOrDefault(emptyList())
        val moduleInfo = modules.find { info -> info.id == moduleId }
        if (moduleInfo == null) {
            me.weishu.kernelsu.ui.component.DynamicIslandManager.show(noModule.format(moduleId))
            onExit()
            return@LaunchedEffect
        }
        if (!moduleInfo.hasActionScript) {
            onExit()
            return@LaunchedEffect
        }
        if (!moduleInfo.enabled || moduleInfo.update || moduleInfo.remove) {
            me.weishu.kernelsu.ui.component.DynamicIslandManager.show(moduleUnavailable.format(moduleInfo.name))
            onExit()
            return@LaunchedEffect
        }
        var actionResult: Boolean
        var currentText = text
        val mainHandler = Handler(Looper.getMainLooper())
        withContext(Dispatchers.IO) {
            runModuleAction(
                moduleId = moduleId,
                onStdout = {
                    val tempText = "$it\n"
                    if (tempText.startsWith("[H[J")) { // clear command
                        currentText = tempText.substring(6)
                    } else {
                        currentText += tempText
                    }
                    mainHandler.post {
                        onTextUpdate(currentText)
                    }
                    logContent.append(it).append("\n")
                },
                onStderr = {
                    logContent.append(it).append("\n")
                }
            ).let {
                actionResult = it
            }
        }
        if (actionResult) {
            if (fromShortcut) {
                me.weishu.kernelsu.ui.component.DynamicIslandManager.show(moduleActionSuccess)
            }
            onExit()
        }
    }
}

fun saveLog(
    logContent: StringBuilder,
    context: Context,
    scope: CoroutineScope
): () -> Unit {
    return {
        scope.launch {
            val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
            val date = format.format(Date())
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "KernelSU_module_action_log_${date}.log"
            )
            file.writeText(logContent.toString())
            me.weishu.kernelsu.ui.component.DynamicIslandManager.show("Log saved to ${file.absolutePath}")
        }
    }
}
