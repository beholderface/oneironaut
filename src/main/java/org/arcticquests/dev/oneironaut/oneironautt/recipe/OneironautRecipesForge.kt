package org.arcticquests.dev.oneironaut.oneironautt.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import java.util.function.BiConsumer


object OneironautRecipesForge {
    private val SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, Oneironaut.MODID)

    private val TYPES: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(Registries.RECIPE_TYPE, Oneironaut.MODID)

    @JvmStatic
    fun init(modBus: IEventBus) {
        OneironautRecipeSerializer.registerSerializers(BiConsumer { serializer, id ->
            require(id.namespace == Oneironaut.MODID) { "Wrong namespace for serializer id: $id" }
            SERIALIZERS.register(id.path) { serializer }
        })

        OneironautRecipeTypes.registerTypes(BiConsumer { type, id ->
            require(id.namespace == Oneironaut.MODID) { "Wrong namespace for recipe type id: $id" }
            TYPES.register(id.path) { type }
        })

        SERIALIZERS.register(modBus)
        TYPES.register(modBus)
    }
}