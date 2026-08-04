package com.soreverse.mcp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.soreverse.mcp.core.GitHubRelease
import com.soreverse.mcp.core.GitHubUpdateManager
import com.soreverse.mcp.core.SettingsStore

private fun settingsTitle(t: UiText, dest: SettingsDest): String = when (dest) {
    SettingsDest.Root -> t.settings
    SettingsDest.ServiceConfig -> if (t.zh) "服务配置" else "Service Configuration"
    SettingsDest.Appearance -> if (t.zh) "外观与语言" else "Appearance"
    SettingsDest.KeepAlive -> t.keepAlive
    SettingsDest.Access -> if (t.zh) "MCP 访问控制" else "MCP Access"
    SettingsDest.Limits -> if (t.zh) "返回数量" else "Result Limits"
    SettingsDest.Export -> if (t.zh) "导出" else "Export"
    SettingsDest.Audit -> if (t.zh) "编辑校验与审计" else "Edit & Audit"
    SettingsDest.Blutter -> "Blutter"
    SettingsDest.Tunnel -> if (t.zh) "Cloudflare 隧道" else "Cloudflare Tunnel"
    SettingsDest.ApkBridge -> if (t.zh) "APK MCP 桥接" else "APK MCP Bridge"
    SettingsDest.AiDeep -> if (t.zh) "AI 深度分析" else "AI Deep Analysis"
    SettingsDest.Updates -> if (t.zh) "版本更新" else "Software Update"
    SettingsDest.Probe -> t.externalProbe
    SettingsDest.ToolStats -> if (t.zh) "工具调用审计" else "Tool Call Audit"
    SettingsDest.TunnelStats -> if (t.zh) "隧道稳定性" else "Tunnel Stability"
    SettingsDest.Instructions -> t.instructions
    SettingsDest.Credits -> if (t.zh) "开源致谢" else "Credits"
    SettingsDest.Disclaimer -> t.disclaimer
    SettingsDest.About -> t.about
    SettingsDest.BackupRestore -> t.backupRestore
}

@Composable
private fun SettingsTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        tint.copy(alpha = 0.16f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    ),
                ),
            )
            .border(BorderStroke(1.dp, tint.copy(alpha = 0.35f)), shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        listOf(tint.copy(alpha = 0.35f), tint.copy(alpha = 0.12f)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleSmall)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun SettingsHub(
    modifier: Modifier = Modifier,
    backProgress: Float,
    t: UiText,
    settings: SettingsStore,
    updateManager: GitHubUpdateManager,
    availableRelease: GitHubRelease?,
    onRelease: (GitHubRelease?) -> Unit,
    language: String,
    onLanguage: (String) -> Unit,
    themeMode: String,
    onTheme: (String) -> Unit,
    accentColor: String,
    onAccent: (String) -> Unit,
    pureBlackDark: Boolean,
    onPureBlack: (Boolean) -> Unit,
    uiDensity: String,
    onDensity: (String) -> Unit,
    cornerStyle: String,
    onCorner: (String) -> Unit,
    motionMode: String,
    onMotion: (String) -> Unit,
    showAdvancedHome: Boolean,
    onShowAdvancedHome: (Boolean) -> Unit,
    highContrast: Boolean,
    onHighContrast: (Boolean) -> Unit,
    textScale: String,
    onTextScale: (String) -> Unit,
    predictiveBack: Boolean,
    onPredictiveBack: (Boolean) -> Unit,
    dest: SettingsDest,
    onDest: (SettingsDest) -> Unit,
    onBack: () -> Unit,
    onHome: (() -> Unit)? = null,
) {
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = t.settings,
                subtitle = if (t.zh) "常用在前，极客选项更深一层" else "Common first, power options deeper",
                showBack = onHome != null,
                onBack = onHome,
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = LocalUiMetrics.current.pagePad)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // 逆核: 小白向精简 —— 常用只留服务配置/保活,其余收进"高级"。
                Text(if (t.zh) "常用" else "Essentials", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingsTile(if (t.zh) "服务配置" else "Service", if (t.zh) "端口 / 工作目录 / 地址" else "Port / directory / URLs", Icons.Default.Settings, MaterialTheme.colorScheme.primary, { onDest(SettingsDest.ServiceConfig) }, Modifier.weight(1f))
                    SettingsTile(if (t.zh) "保活" else "Keep-alive", if (t.zh) "后台常驻 / 自启" else "Wake lock / boot", Icons.Default.PowerSettingsNew, AppPalette.green, { onDest(SettingsDest.KeepAlive) }, Modifier.weight(1f))
                }

                // 高级(极客/按需)—— 折叠在后,普通用户用不到
                Text(if (t.zh) "高级(按需)" else "Advanced", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                SurfacePanel {
                    NavRow(if (t.zh) "外观与语言" else "Appearance", if (t.zh) "主题 / 强调色 / 字号" else "Theme / accent / text", Icons.Default.Tune, onClick = { onDest(SettingsDest.Appearance) })
                    GroupDivider()
                    NavRow(if (t.zh) "AI 深度分析" else "AI Deep", if (t.zh) "内置 AI 分析的端点 / Key" else "Endpoint / key / model", Icons.Default.Memory, onClick = { onDest(SettingsDest.AiDeep) })
                    GroupDivider()
                    NavRow(if (t.zh) "公网隧道" else "Tunnel", if (t.zh) "让电脑/远程也能连(Cloudflare)" else "Public expose", Icons.Default.Cloud, onClick = { onDest(SettingsDest.Tunnel) })
                    GroupDivider()
                    NavRow("APK MCP", if (t.zh) "桥接 MT 管理器(可选)" else "MT Manager bridge", Icons.Default.Link, onClick = { onDest(SettingsDest.ApkBridge) })
                    GroupDivider()
                    NavRow(if (t.zh) "引擎参数" else "Engine limits", if (t.zh) "返回数量 / 导出 / 审计 / Blutter" else "Limits / export / audit / Blutter", Icons.Default.Analytics, onClick = { onDest(SettingsDest.Limits) })
                }

                Text(if (t.zh) "关于" else "About", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                SurfacePanel {
                    NavRow(t.backupRestore, t.backupRestoreSubtitle, Icons.Default.Storage, onClick = { onDest(SettingsDest.BackupRestore) })
                    GroupDivider()
                    NavRow(t.instructions, if (t.zh) "推荐工作流" else "Workflow", Icons.Default.Description, onClick = { onDest(SettingsDest.Instructions) })
                    GroupDivider()
                    NavRow(t.about, if (t.zh) "版本与信息" else "Version & info", icon = Icons.Default.Info, onClick = { onDest(SettingsDest.About) })
                    GroupDivider()
                    NavRow(t.disclaimer, if (t.zh) "使用须知" else "Disclaimer", icon = Icons.Default.Security, onClick = { onDest(SettingsDest.Disclaimer) })
                }
                // License footer
                Text(
                    "${com.soreverse.mcp.core.Provenance.PROJECT} · ${com.soreverse.mcp.core.Provenance.LICENSE}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp).align(Alignment.CenterHorizontally),
                )
            }
        }
        if (dest != SettingsDest.Root) {
    Surface(
        modifier = Modifier.fillMaxSize().graphicsLayer {
            translationX = size.width * backProgress
            alpha = 1f - 0.12f * backProgress
        },
        color = MaterialTheme.colorScheme.background,
    ) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            title = settingsTitle(t, dest),
            showBack = true,
            onBack = onBack,
        )
        Box(Modifier.fillMaxSize()) {
        when (dest) {
            SettingsDest.ServiceConfig -> SettingsServiceConfigPage(t, settings)
            SettingsDest.Appearance -> SettingsAppearancePage(t, language, onLanguage, themeMode, onTheme, accentColor, onAccent, pureBlackDark, onPureBlack, uiDensity, onDensity, cornerStyle, onCorner, motionMode, onMotion, showAdvancedHome, onShowAdvancedHome, highContrast, onHighContrast, textScale, onTextScale, predictiveBack, onPredictiveBack)
            SettingsDest.KeepAlive -> SettingsKeepAlivePage(t, settings)
            SettingsDest.Access -> SettingsAccessPage(t, settings)
            SettingsDest.Limits -> SettingsLimitsPage(t, settings)
            SettingsDest.Export -> SettingsExportPage(t, settings)
            SettingsDest.Audit -> SettingsAuditPage(t, settings)
            SettingsDest.Blutter -> SettingsBlutterPage(t)
            SettingsDest.Tunnel -> SettingsTunnelPage(t, settings)
            SettingsDest.ApkBridge -> SettingsApkBridgePage(t, settings)
            SettingsDest.AiDeep -> SettingsAiDeepPage(t, settings)
            SettingsDest.Updates -> SettingsUpdatesPage(t, settings, updateManager, availableRelease, onRelease)
            SettingsDest.Probe -> SettingsProbePage(t, settings)
            SettingsDest.BackupRestore -> SettingsBackupRestorePage(t, settings)
            SettingsDest.ToolStats -> PageScroll { GlassGroup { Column(Modifier.padding(12.dp)) { ToolStatsSection(t, settings) } } }
            SettingsDest.TunnelStats -> PageScroll { GlassGroup { Column(Modifier.padding(12.dp)) { TunnelStatsSection(t) } } }
            SettingsDest.Instructions -> PageScroll {
                GlassGroup {
                    Text(t.instructionsBody, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (t.zh)
                            "详细流程：先在服务页启动 SO MCP；需要电脑访问时开启 Cloudflare Tunnel，或 adb forward tcp:8000 tcp:8000。客户端配置 /mcp 后按 so_open -> analyze_* -> read_disasm/search_* 分析；修改前 session_open，dryRun 预览后再 patch，最后 build_so 导出。"
                        else
                            "Start SO MCP on Service tab; enable Cloudflare Tunnel or adb forward for desktop access; then follow so_open -> analyze_* -> read/search -> session_open -> dryRun patch -> build_so.",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            SettingsDest.Credits -> SettingsCreditsPage(t)
            SettingsDest.Disclaimer -> PageScroll {
                GlassGroup {
                    Text(t.disclaimerBody, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                }
                GlassGroup(title = if (t.zh) "开源许可" else "License") {
                    Text(
                        if (t.zh) {
                            "${com.soreverse.mcp.core.Provenance.PROJECT} 是 GPL-3.0-only 自由软件。任何再分发（含修改、改名、二次打包版本）必须保留本版权与许可声明、继续以 GPL-3.0-only 授权，并向每一位接收者提供完整对应源代码。"
                        } else {
                            "${com.soreverse.mcp.core.Provenance.PROJECT} is GPL-3.0-only free software. Any redistribution must retain this notice, remain under GPL-3.0-only, and provide complete corresponding source code."
                        },
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            SettingsDest.About -> {
                val aboutContext = LocalContext.current
                PageScroll {
                    GlassGroup {
                        Text(t.aboutBody, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                        Row(Modifier.padding(14.dp)) {
                            PrimaryActionButton(t.joinQqGroup, { joinQqGroup(aboutContext, t.zh) }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
            SettingsDest.Root -> Unit
        }
        }
    }
    }
    }
}
}
