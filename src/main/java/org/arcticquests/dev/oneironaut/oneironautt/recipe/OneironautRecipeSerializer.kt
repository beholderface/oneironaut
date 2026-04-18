package org.arcticquests.dev.oneironaut.oneironautt.recipe;

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import java.util.function.BiConsumer

class OneironautRecipeSerializer {
    companion object {
        @JvmStatic
        fun registerSerializers(r: BiConsumer<RecipeSerializer<*>, ResourceLocation>) {
            for ((key, value) in SERIALIZERS) {
                r.accept(value, key)
            }
        }

        private val SERIALIZERS: MutableMap<ResourceLocation, RecipeSerializer<*>> = LinkedHashMap()

        val INFUSE: RecipeSerializer<*> = register("infuse", InfusionRecipe.Serializer())

        private fun <T : Recipe<*>?> register(name: String, rs: RecipeSerializer<T>): RecipeSerializer<T> {
            val old = SERIALIZERS.put(Oneironaut.id(name), rs)
            require(old == null) { "Typo? Duplicate id $name" }
            return rs
        }
    }
}