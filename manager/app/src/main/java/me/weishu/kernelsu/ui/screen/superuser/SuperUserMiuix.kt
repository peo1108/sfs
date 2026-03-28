package me.weishu.kernelsu.ui.screen.superuser

import android.content.pm.ApplicationInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.setValue
import com.kyant.capsule.ContinuousRoundedRectangle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeSource
import me.weishu.kernelsu.R
import me.weishu.kernelsu.data.model.AppInfo
import me.weishu.kernelsu.ui.component.AppIconImage
import me.weishu.kernelsu.ui.component.miuix.SearchBox
import me.weishu.kernelsu.ui.component.miuix.SearchPager
import me.weishu.kernelsu.ui.component.statustag.StatusTag
import me.weishu.kernelsu.ui.theme.LocalEnableBlur
import me.weishu.kernelsu.ui.theme.isInDarkTheme
import me.weishu.kernelsu.ui.util.rememberGyroTilt
import me.weishu.kernelsu.ui.util.rememberGyroGlowBrush
import me.weishu.kernelsu.ui.util.doubleBezelCard
import me.weishu.kernelsu.ui.util.pressScale
import me.weishu.kernelsu.ui.util.rememberGyroRadialGlow
import me.weishu.kernelsu.ui.util.ownerNameForUid
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.extra.SuperListPopup
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.MoreCircle
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun SuperUserPagerMiuix(
    uiState: SuperUserUiState,
    actions: SuperUserActions,
    bottomInnerPadding: Dp,
) {
    val searchStatus = uiState.searchStatus
    val enableBlur = LocalEnableBlur.current

    val scrollBehavior = MiuixScrollBehavior()
    val dynamicTopPadding by remember {
        derivedStateOf { 12.dp * (1f - scrollBehavior.state.collapsedFraction) }
    }

    val hazeState = remember { HazeState() }
    val hazeStyle = if (enableBlur) {
        HazeStyle(
            backgroundColor = Color.Transparent,
            tint = HazeTint(colorScheme.surface.copy(0.05f)),
            blurRadius = 25.dp
        )
    } else {
        HazeStyle.Unspecified
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            searchStatus.TopAppBarAnim(hazeState = hazeState, hazeStyle = hazeStyle) {
                TopAppBar(
                    color = Color.Transparent,
                    title = stringResource(R.string.superuser),
                    actions = {
                        val showTopPopup = remember { mutableStateOf(false) }
                        SuperListPopup(
                            show = showTopPopup.value,
                            popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                            alignment = PopupPositionProvider.Align.TopEnd,
                            onDismissRequest = {
                                showTopPopup.value = false
                            },
                            content = {
                                val isMultiUser = uiState.userIds.size > 1
                                val size = if (isMultiUser) 2 else 1
                                ListPopupColumn {
                                    DropdownImpl(
                                        text = stringResource(R.string.show_system_apps),
                                        isSelected = uiState.showSystemApps,
                                        optionSize = size,
                                        onSelectedIndexChange = {
                                            actions.onToggleShowSystemApps()
                                            showTopPopup.value = false
                                        },
                                        index = 0
                                    )
                                    if (isMultiUser) {
                                        DropdownImpl(
                                            text = stringResource(R.string.show_only_primary_user_apps),
                                            isSelected = uiState.showOnlyPrimaryUserApps,
                                            optionSize = size,
                                            onSelectedIndexChange = {
                                                actions.onToggleShowOnlyPrimaryUserApps()
                                                showTopPopup.value = false
                                            },
                                            index = 1
                                        )
                                    }
                                }
                            }
                        )
                        IconButton(
                            modifier = Modifier.padding(end = 16.dp),
                            onClick = {
                                showTopPopup.value = true
                            },
                            holdDownState = showTopPopup.value
                        ) {
                            Icon(
                                imageVector = MiuixIcons.MoreCircle,
                                tint = colorScheme.onSurface,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        popupHost = {
            val expandedSearchUids = remember { mutableStateOf(setOf<Int>()) }
            LaunchedEffect(uiState.searchResults) {
                expandedSearchUids.value = uiState.searchResults
                    .filter { it.apps.size > 1 }
                    .map { it.uid }
                    .toSet()
            }
            searchStatus.SearchPager(
                onSearchStatusChange = actions.onSearchStatusChange,
                hazeState = hazeState,
                hazeStyle = hazeStyle,
                defaultResult = {},
                searchBarTopPadding = dynamicTopPadding,
            ) {
                val imeBottomPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .overScrollVertical(),
                ) {
                    item {
                        Spacer(Modifier.height(6.dp))
                    }
                    items(uiState.searchResults, key = { it.uid }, contentType = { "group" }) { group ->
                        val expanded = expandedSearchUids.value.contains(group.uid)
                        AnimatedVisibility(
                            visible = uiState.searchResults.isNotEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                GroupItem(
                                    group = group,
                                    onToggleExpand = {
                                        if (group.apps.size > 1) {
                                            expandedSearchUids.value =
                                                if (expanded) expandedSearchUids.value - group.uid else expandedSearchUids.value + group.uid
                                        }
                                    },
                                ) {
                                    actions.onOpenProfile(group)
                                }
                                AnimatedVisibility(
                                    visible = expanded && group.apps.size > 1,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        group.apps.forEach { app ->
                                            SimpleAppItem(
                                                app = app,
                                                matched = group.matchedPackageNames.contains(app.packageName),
                                            )
                                        }
                                        Spacer(Modifier.height(6.dp))
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.height(maxOf(bottomInnerPadding, imeBottomPadding)))
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        searchStatus.SearchBox(
            onSearchStatusChange = actions.onSearchStatusChange,
            searchBarTopPadding = dynamicTopPadding,
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(layoutDirection),
                end = innerPadding.calculateEndPadding(layoutDirection)
            ),
            hazeState = hazeState,
            hazeStyle = hazeStyle
        ) { boxHeight ->
            val lazyListState = rememberLazyListState()
            val prevRefreshing = remember { booleanArrayOf(false) }
            if (prevRefreshing[0] && !uiState.isRefreshing) {
                lazyListState.requestScrollToItem(0)
            }
            prevRefreshing[0] = uiState.isRefreshing
            val pullToRefreshState = rememberPullToRefreshState()
            val refreshTexts = listOf(
                stringResource(R.string.refresh_pulling),
                stringResource(R.string.refresh_release),
                stringResource(R.string.refresh_refresh),
                stringResource(R.string.refresh_complete),
            )

            if (uiState.groupedApps.isEmpty() && !uiState.hasLoaded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding.calculateStartPadding(layoutDirection),
                            end = innerPadding.calculateEndPadding(layoutDirection),
                            bottom = bottomInnerPadding
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    InfiniteProgressIndicator()
                }
            } else {
                val expandedUids = remember { mutableStateOf(setOf<Int>()) }
                PullToRefresh(
                    isRefreshing = uiState.isRefreshing,
                    pullToRefreshState = pullToRefreshState,
                    onRefresh = actions.onRefresh,
                    refreshTexts = refreshTexts,
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding() + boxHeight.value + 6.dp,
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection)
                    ),
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxHeight()
                            .scrollEndHaptic()
                            .overScrollVertical()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .let { if (enableBlur) it.hazeSource(state = hazeState) else it },
                        contentPadding = PaddingValues(
                            top = innerPadding.calculateTopPadding() + boxHeight.value + 6.dp,
                            start = innerPadding.calculateStartPadding(layoutDirection),
                            end = innerPadding.calculateEndPadding(layoutDirection)
                        ),
                                            ) {
                        items(uiState.groupedApps, key = { it.uid }, contentType = { "group" }) { group ->
                            val expanded = expandedUids.value.contains(group.uid)
                            Column {
                                GroupItem(
                                    group = group,
                                    onToggleExpand = {
                                        if (group.apps.size > 1) {
                                            expandedUids.value =
                                                if (expanded) expandedUids.value - group.uid else expandedUids.value + group.uid
                                        }
                                    }
                                ) {
                                    actions.onOpenProfile(group)
                                }
                                AnimatedVisibility(
                                    visible = expanded && group.apps.size > 1,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        group.apps.forEach { app ->
                                            SimpleAppItem(app = app)
                                        }
                                        Spacer(Modifier.height(6.dp))
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(bottomInnerPadding))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleAppItem(
    app: AppInfo,
    matched: Boolean = false,
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800, 
                easing = FastOutSlowInEasing
            )
        )
    }

    Row(
        modifier = Modifier.graphicsLayer {
            val inverseProgress = 1f - animatedProgress.value
            alpha = animatedProgress.value
            translationX = inverseProgress * 400.dp.toPx()
            translationY = inverseProgress * 600.dp.toPx()
            scaleX = 0.9f + 0.1f * animatedProgress.value
            scaleY = 0.9f + 0.1f * animatedProgress.value
        }
    ) {
        Box(
            modifier = Modifier
                .padding(start = 12.dp)
                .width(6.dp)
                .height(24.dp)
                .align(Alignment.CenterVertically)
                .clip(ContinuousRoundedRectangle(16.dp))
                .background(if (matched) colorScheme.primary else colorScheme.primaryContainer)
        )
        // iOS 26: SimpleAppItem glow border with gyro
        val miniCardShape = ContinuousRoundedRectangle(16.dp)
        val tiltValue = me.weishu.kernelsu.ui.util.LocalGyroTilt.current
        val miniGlowBrush = rememberGyroGlowBrush(
            primaryColor = Color.White,
            accentColor = colorScheme.primary,
            tilt = tiltValue
        )
        val radialGlow = rememberGyroRadialGlow(tilt = tiltValue)
        Card(
            modifier = Modifier
                .padding(start = 6.dp, end = 12.dp, bottom = 6.dp)
                .pressScale()
                .doubleBezelCard(
                    shape = miniCardShape,
                    glowBrush = miniGlowBrush,
                    radialGlow = radialGlow,
                ),
            colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f))
        ) {
            BasicComponent(
                title = app.label,
                summary = app.packageName,
                startAction = {
                    AppIconImage(
                        packageInfo = app.packageInfo,
                        label = app.label,
                        modifier = Modifier
                            .padding(end = 9.dp)
                            .size(40.dp)
                    )
                },
                insideMargin = PaddingValues(horizontal = 9.dp)
            )
        }
    }
}

@Composable
private fun GroupItem(
    group: GroupedApps,
    onToggleExpand: () -> Unit,
    onClickPrimary: () -> Unit,
) {
    val isInDarkTheme = isInDarkTheme()
    val bg = colorScheme.secondaryContainer.copy(alpha = 0.8f)
    val rootBg = colorScheme.tertiaryContainer.copy(alpha = 0.6f)
    val unmountBg = if (isInDarkTheme) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.3f)
    val fg = colorScheme.onSecondaryContainer
    val rootFg = colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
    val unmountFg = if (isInDarkTheme) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.8f)

    val userId = group.uid / 100000
    val packageInfo = group.primary.packageInfo
    val applicationInfo = packageInfo.applicationInfo
    val hasSharedUserId = !packageInfo.sharedUserId.isNullOrEmpty()
    val isSystemApp = applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0
            || applicationInfo?.flags?.and(ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    val tags = remember(group.anyAllowSu, group.shouldUmount, group.anyCustom, userId, isSystemApp, hasSharedUserId) {
        buildList {
            if (group.anyAllowSu) add(StatusMeta("ROOT", rootBg, rootFg))
            if (group.shouldUmount) add(StatusMeta("UMOUNT", unmountBg, unmountFg))
            if (group.anyCustom) add(StatusMeta("CUSTOM", bg, fg))
            if (userId != 0) add(StatusMeta("USER $userId", bg, fg))
            if (isSystemApp) add(StatusMeta("SYSTEM", bg, fg))
            if (hasSharedUserId) add(StatusMeta("SHARED UID", bg, fg))
        }
    }

    val animatedProgress = remember { Animatable(0f) }
    var showContextMenu by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800, 
                easing = FastOutSlowInEasing
            )
        )
    }

    // iOS 26: GroupItem glow border with real-time gyro reflection
    val miniCardShape = ContinuousRoundedRectangle(16.dp)
    val tiltValue = me.weishu.kernelsu.ui.util.LocalGyroTilt.current
    val miniGlowBrush = rememberGyroGlowBrush(
        primaryColor = Color.White,
        accentColor = colorScheme.primary,
        tilt = tiltValue
    )
    val radialGlow = rememberGyroRadialGlow(tilt = tiltValue)

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .pressScale()
            .graphicsLayer {
                val inverseProgress = 1f - animatedProgress.value
                alpha = animatedProgress.value
                translationX = inverseProgress * 400.dp.toPx()
                translationY = inverseProgress * 600.dp.toPx()
                scaleX = 0.9f + 0.1f * animatedProgress.value
                scaleY = 0.9f + 0.1f * animatedProgress.value
            }
            .doubleBezelCard(
                shape = miniCardShape,
                glowBrush = miniGlowBrush,
                radialGlow = radialGlow,
            ),
        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = colorScheme.surface.copy(alpha = 0.08f)),
        onClick = onClickPrimary,
        onLongPress = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showContextMenu = true
        },
        showIndication = true,
        insideMargin = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconImage(
                packageInfo = group.primary.packageInfo,
                label = group.primary.label,
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(48.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = if (group.apps.size > 1) ownerNameForUid(group.uid) else group.primary.label,
                    modifier = Modifier.basicMarquee(),
                    fontWeight = FontWeight(550),
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = if (group.apps.size > 1) {
                        stringResource(R.string.group_contains_apps, group.apps.size)
                    } else {
                        group.primary.packageName
                    },
                    modifier = Modifier
                        .basicMarquee(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight(550),
                    color = colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                    softWrap = false
                )
            }
            if (tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 3.dp, bottom = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tags.forEach { tag ->
                        StatusTag(
                            label = tag.label,
                            backgroundColor = tag.bg,
                            contentColor = tag.fg
                        )
                    }
                }
            }
            val layoutDirection = LocalLayoutDirection.current
            Image(
                modifier = Modifier
                    .graphicsLayer {
                        if (layoutDirection == LayoutDirection.Rtl) scaleX = -1f
                    }
                    .padding(start = 8.dp)
                    .size(width = 10.dp, height = 16.dp),
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions),
            )
        }
        
        SuperListPopup(
            show = showContextMenu,
            popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
            alignment = PopupPositionProvider.Align.TopEnd,
            onDismissRequest = { showContextMenu = false },
            content = {
                val hasMultipleApps = group.apps.size > 1
                val actionsSize = if (hasMultipleApps) 2 else 1
                ListPopupColumn {
                    DropdownImpl(
                        text = stringResource(R.string.profile),
                        optionSize = actionsSize,
                        isSelected = false,
                        onSelectedIndexChange = {
                            showContextMenu = false
                            onClickPrimary()
                        },
                        index = 0
                    )
                    if (hasMultipleApps) {
                        DropdownImpl(
                            text = "Expand/Collapse",
                            optionSize = actionsSize,
                            isSelected = false,
                            onSelectedIndexChange = {
                                showContextMenu = false
                                onToggleExpand()
                            },
                            index = 1
                        )
                    }
                }
            }
        )
    }
}

@Immutable
private data class StatusMeta(
    val label: String,
    val bg: Color,
    val fg: Color
)
