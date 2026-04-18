package org.arcticquests.dev.oneironaut.oneironautt.recipe
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.resources.ResourceLocation
import java.util.function.BiConsumer
import net.minecraft.core.Registry
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut.MODID
import kotlin.collections.iterator


class OneironautRecipeTypes {
    companion object {
        const val debugMessages = false

        @JvmStatic
        fun registerTypes(r: BiConsumer<RecipeType<*>, ResourceLocation>) {
            for ((key, value) in TYPES) {
                Oneironaut.boolLogger("Attempting to register type $value with key $key", debugMessages)
                r.accept(value, key)
            }
        }

        private val TYPES: MutableMap<ResourceLocation, RecipeType<*>> = LinkedHashMap()

        @JvmStatic
        var INFUSION_TYPE: RecipeType<InfusionRecipe> = registerType("infuse")

        private fun <T : Recipe<*>> registerType(name: String): RecipeType<T> {
            val type: RecipeType<T> = object : RecipeType<T> {
                override fun toString(): String = "$MODID:$name"
            }
            TYPES[Oneironaut.id(name)] = type
            Oneironaut.boolLogger("Attempting to register type $name, with id ${type.toString()}", debugMessages)
            return type
        }

    }
}