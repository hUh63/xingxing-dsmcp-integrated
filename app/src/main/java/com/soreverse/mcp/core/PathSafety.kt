package com.soreverse.mcp.core

import java.io.File

/**
 * 安全修复: 路径穿越防护工具。
 *
 * 验证文件路径参数不会通过 ../ 等方式逃逸出允许的工作目录，
 * 防止 MCP 工具读取/写入设备上的任意文件（如 /data/system/password.key）。
 */
object PathSafety {

    /** 允许的根目录列表（路径参数必须解析到其中之一）。 */
    private val ALLOWED_ROOTS = listOf(
        "/storage/emulated/0",
        "/sdcard",
        "/data/data/com.soreverse.mcp",
        "/data/user/0/com.soreverse.mcp",
        "/data/local/tmp",
        "/data/user/de/0/com.soreverse.mcp",
        "/data/user/0/com.soreverse.mcp/files",
        System.getProperty("java.io.tmpdir", "/tmp"),
    )

    /**
     * 验证路径是否安全（不包含路径穿越，且在允许的目录范围内）。
     *
     * @param path 用户提供的文件路径
     * @param workDirPath 可选的工作目录路径，如果提供则也允许该目录下的文件
     * @return true 如果路径安全
     */
    fun isSafe(path: String, workDirPath: String? = null): Boolean {
        if (path.isBlank()) return false
        // 检查路径穿越字符序列
        if (path.contains("../") || path.contains("..\\") || path.contains("/..")) return false
        // 解析为规范路径
        val canonical = runCatching { File(path).canonicalPath }.getOrNull() ?: return false
        // 检查是否在允许的根目录下
        val roots = ALLOWED_ROOTS.toMutableList()
        if (!workDirPath.isNullOrBlank()) {
            roots.add(File(workDirPath).canonicalPath)
        }
        return roots.any { root -> canonical.startsWith(root) }
    }

    /**
     * 验证路径，如果不安全则返回错误消息。
     *
     * @param path 用户提供的文件路径
     * @param workDirPath 可选的工作目录路径
     * @return null 如果路径安全，否则返回错误消息字符串
     */
    fun validate(path: String, workDirPath: String? = null): String? {
        if (path.isBlank()) return "Path is blank"
        if (path.contains("../") || path.contains("..\\") || path.contains("/..")) {
            return "Path contains directory traversal sequence (../): $path"
        }
        val canonical = runCatching { File(path).canonicalPath }.getOrNull()
            ?: return "Cannot resolve path: $path"
        val roots = ALLOWED_ROOTS.toMutableList()
        if (!workDirPath.isNullOrBlank()) {
            runCatching { roots.add(File(workDirPath).canonicalPath) }
        }
        if (!roots.any { root -> canonical.startsWith(root) }) {
            return "Path is outside allowed directories: $path (resolved: $canonical)"
        }
        return null
    }
}
