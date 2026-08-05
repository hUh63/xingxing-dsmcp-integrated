package com.soreverse.mcp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

// 玄星算力 QQ 交流群加群短链(新版手Q q 短链, 系统会自动唤起手Q加群界面)。
private const val QQ_GROUP_URL = "https://qm.qq.com/q/gOidrbZDsO"

internal fun joinQqGroup(context: Context, zh: Boolean) {
    // 优先让手Q/TIM 直接处理该加群链接, 失败再交给系统浏览器兜底。
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(QQ_GROUP_URL))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    for (pkg in listOf("com.tencent.mobileqq", "com.tencent.tim", null)) {
        val i = Intent(intent)
        if (pkg != null) i.setPackage(pkg)
        runCatching {
            context.startActivity(i)
            return
        }
    }
    Toast.makeText(
        context,
        if (zh) "无法打开加群链接，请手动复制：$QQ_GROUP_URL" else "Cannot open group link. Copy manually: $QQ_GROUP_URL",
        Toast.LENGTH_LONG,
    ).show()
}
