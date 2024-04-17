package kr.toxicity.hud.manager

import kr.toxicity.hud.api.manager.PopupManager
import kr.toxicity.hud.api.popup.Popup
import kr.toxicity.hud.popup.PopupImpl
import kr.toxicity.hud.resource.GlobalResource
import kr.toxicity.hud.util.*
import java.util.*
import kotlin.collections.HashMap

object PopupManagerImpl: BetterHudManager, PopupManager {
    private val popupMap = HashMap<String, PopupImpl>()
    override fun start() {

    }

    override fun reload(resource: GlobalResource, callback: () -> Unit) {
        popupMap.clear()
        val save = resource.font.subFolder("popup")
        DATA_FOLDER.subFolder("popups").forEachAllYamlAsync({ _, file, s, configurationSection ->
            runCatching {
                popupMap[s] = PopupImpl(save, s, configurationSection)
            }.onFailure { e ->
                warn("Unable to load this popup: $s in ${file.name}")
                warn("Reason: ${e.message}")
            }
        }, callback)
    }

    override fun getAllNames(): MutableSet<String> = Collections.unmodifiableSet(popupMap.keys)
    override fun getPopup(name: String): Popup? = popupMap[name]
    override fun getDefaultPopups(): Set<Popup> = popupMap.values.filter {
        it.isDefault
    }.toSet()
    override fun end() {
    }
}