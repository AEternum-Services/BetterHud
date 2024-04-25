package kr.toxicity.hud.configuration

import kr.toxicity.hud.util.*
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

enum class PluginConfiguration(
    private val dir: String
) {
    CONFIG("config.yml"),
    DATABASE("database.yml"),
    SHADER("shader.yml")
    ;

    fun create(): YamlConfiguration {
        val file = File(DATA_FOLDER, dir)
        val exists = file.exists()
        if (!exists) PLUGIN.saveResource(dir, false)
        val yaml = file.toYaml()
        val version = yaml.getString("plugin-version") ?: "unknown"
        if (exists && PLUGIN.description.version != version) {
            warn(
                "Old configuration version found: $dir in $version",
                "Configuration will be automatically updated."
            )
            val newYaml = PLUGIN.getResource(dir).ifNull("Resource '$dir' not found.").toYaml()
            yaml.getKeys(true).forEach {
                if (!newYaml.contains(it)) yaml.set(it, null)
            }
            newYaml.getKeys(true).forEach {
                if (!yaml.contains(it)) {
                    yaml.set(it, newYaml.get(it))
                    yaml.setComments(it ,newYaml.getComments(it))
                    yaml.setInlineComments(it, newYaml.getComments(it))
                }
            }
        }
        yaml.set("plugin-version", PLUGIN.description.version)
        return yaml.apply {
            save(file)
        }
    }
}
