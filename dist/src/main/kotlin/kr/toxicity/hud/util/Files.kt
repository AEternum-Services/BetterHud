package kr.toxicity.hud.util

import org.bukkit.configuration.ConfigurationSection
import java.io.File
import java.util.concurrent.CompletableFuture

fun File.subFolder(dir: String) = File(this, dir).apply {
    if (!exists()) mkdir()
}

fun File.subFile(name: String) = File(this, name).apply {
    if (!exists()) createNewFile()
}

fun File.ifNotExist(message: String) = apply {
    if (!exists()) throw RuntimeException(message)
}

fun File.clearFolder() = apply {
    deleteRecursively()
    mkdir()
}

fun File.forEach(block: (File) -> Unit) {
    listFiles()?.forEach(block)
}

fun File.forEachAllFolder(block: (File) -> Unit) {
    if (isDirectory) forEach {
        it.forEachAllFolder(block)
    } else {
        block(this)
    }
}

fun File.forEachAllFolderAsync(block: (File) -> Unit, callback: () -> Unit) {
    fun getAll(file: File): List<File> {
        return if (file.isDirectory) {
            file.listFiles()?.map { subFile ->
                getAll(subFile)
            }?.sum() ?: ArrayList()
        } else {
            listOf(file)
        }
    }
    getAll(this).forEachAsync(block, callback)
}

fun File.forEachAsync(block: (File) -> Unit, callback: () -> Unit) {
    listFiles()?.toList()?.forEachAsync(block, callback) ?: return callback()
}

fun File.forEachAllYaml(block: (File, String, ConfigurationSection) -> Unit) {
    forEachAllFolder {
        if (it.extension == "yml") {
            runCatching {
                it.toYaml().forEachSubConfiguration { s, configurationSection ->
                    block(it, s, configurationSection)
                }
            }.onFailure { e ->
                warn("Unable to load this yml file: ${it.name}")
                warn("Reason: ${e.message}")
            }
        }
    }
}
fun File.forEachAllYamlAsync(block: (File, String, ConfigurationSection) -> Unit, callback: () -> Unit) {
    forEachAllFolderAsync({
        if (it.extension == "yml") {
            runCatching {
                it.toYaml().forEachSubConfiguration { s, configurationSection ->
                    block(it, s, configurationSection)
                }
            }.onFailure { e ->
                warn("Unable to load this yml file: ${it.name}")
                warn("Reason: ${e.message}")
            }
        }
    }, callback)
}