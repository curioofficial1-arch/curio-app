package com.example.ui.components

import com.example.R

object CurioImageHelper {
    fun getDrawableRes(coverKey: String?): Int? {
        return when (coverKey) {
            "hero_brain_dying" -> R.drawable.hero_brain_dying
            "hero_mancity_investigation" -> R.drawable.hero_mancity_investigation
            "hero_curio_cabinet" -> R.drawable.hero_curio_cabinet
            else -> null
        }
    }

    val availableCovers = listOf(
        CoverOption("hero_brain_dying", "Neuroscience / Brain", R.drawable.hero_brain_dying),
        CoverOption("hero_mancity_investigation", "Investigations / Sports", R.drawable.hero_mancity_investigation),
        CoverOption("hero_curio_cabinet", "Curio Cabinet / Artifacts", R.drawable.hero_curio_cabinet)
    )
}

data class CoverOption(
    val key: String,
    val title: String,
    val resId: Int
)
