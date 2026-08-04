package com.soreverse.mcp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.soreverse.mcp.core.SettingsStore
import com.soreverse.mcp.mcp.ToolCatalog

/**
 * 逆核 工具页 — 把所有内置 MCP 工具按分类列给用户看,小白能看懂每个工具干嘛。
 * 数据来源 ToolCatalog.ALL(每个工具的中文/英文说明就是 meta.zh / meta.en)。
 */
@Composable
internal fun ToolsTab(t: UiText, settings: SettingsStore, onBack: (() -> Unit)? = null) {
    // 分类 → 中文标题 + 图标(顺序即展示顺序;未列出的分类归到"更多")
    val categoryMeta: Map<String, Pair<String, ImageVector>> = linkedMapOf(
        "workspace" to ((if (t.zh) "打开文件 / APK" else "Open / APK") to Icons.Filled.DataObject),
        "decompile" to ((if (t.zh) "反编译 / 反汇编" else "Decompile") to Icons.Filled.Code),
        "read" to ((if (t.zh) "查看 / 读取" else "Read") to Icons.Filled.Code),
        "search" to ((if (t.zh) "搜索" else "Search") to Icons.Filled.Memory),
        "analysis" to ((if (t.zh) "分析" else "Analysis") to Icons.Filled.Memory),
        "emulate" to ((if (t.zh) "模拟执行 (unidbg)" else "Emulate") to Icons.Filled.Memory),
        "dynamic" to ((if (t.zh) "动态插桩 (Frida)" else "Dynamic (Frida)") to Icons.Filled.Bolt),
        "edit" to ((if (t.zh) "补丁 / 编辑" else "Patch / Edit") to Icons.Filled.Bolt),
        "build" to ((if (t.zh) "构建 / 导出" else "Build") to Icons.Filled.Bolt),
        "session" to ((if (t.zh) "会话 / 事务" else "Session") to Icons.Filled.Bolt),
        "system" to ((if (t.zh) "系统 / 网关" else "System") to Icons.Filled.Extension),
        "meta" to ((if (t.zh) "工具信息" else "Meta") to Icons.Filled.Extension),
    )

    // 按 category 归组,顺序按 categoryMeta 出现顺序,其余归"更多"
    val grouped = ToolCatalog.ALL.groupBy { it.meta.category }

    PageScroll {
        ScreenHeader(
            title = if (t.zh) "工具" else "Tools",
            subtitle = if (t.zh) "共 ${ToolCatalog.ALL.size} 个工具 · AI 通过 MCP 自动调用" else "${ToolCatalog.ALL.size} tools · called by AI via MCP",
            showBack = onBack != null,
            onBack = onBack,
        )

        // 已知分类按定义顺序展示
        val shownCategories = mutableSetOf<String>()
        for ((cat, meta) in categoryMeta) {
            val tools = grouped[cat] ?: continue
            shownCategories += cat
            ToolCategoryGroup(cat = cat, title = meta.first, icon = meta.second, tools = tools, zh = t.zh)
        }
        // 其余未归类的分类
        val rest = grouped.filterKeys { it !in shownCategories }
        for ((cat, tools) in rest) {
            ToolCategoryGroup(cat = cat, title = cat, icon = Icons.Filled.Extension, tools = tools, zh = t.zh)
        }
    }
}

@Composable
private fun ToolCategoryGroup(
    cat: String,
    title: String,
    icon: ImageVector,
    tools: List<com.soreverse.mcp.mcp.ToolHandler>,
    zh: Boolean,
) {
    GlassGroup(title = title) {
        tools.forEachIndexed { i, tool ->
            if (i > 0) GroupDivider()
            ToolRow(
                name = tool.meta.name,
                desc = if (zh) tool.meta.zh else tool.meta.en,
                icon = icon,
            )
        }
    }
}

@Composable
private fun ToolRow(name: String, desc: String, icon: ImageVector) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                name,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
