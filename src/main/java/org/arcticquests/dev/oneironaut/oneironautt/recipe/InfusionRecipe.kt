package org.arcticquests.dev.oneironaut.oneironautt.recipe;

import at.petrak.hexcasting.common.recipe.RecipeSerializerBase
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredient
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredientHelper
import com.google.gson.JsonObject
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.core.RegistryAccess
import net.minecraft.world.level.Level
import net.minecraft.util.GsonHelper

//pretty much all of this is yoinked from hexal
data class InfusionRecipe(val identifier: ResourceLocation, val blockIn : StateIngredient, val blockOut : BlockState, val mediaCost : Long) : Recipe<Container> {
    override fun matches(inventory: Container, world: Level) = false

    fun matches(blockIn : BlockState): Boolean = this.blockIn.test(blockIn)

    override fun assemble(inventory: Container, registryManager : RegistryAccess): ItemStack = ItemStack.EMPTY

    override fun canCraftInDimensions(width: Int, height: Int) = false

    override fun getResultItem(registryManager : RegistryAccess) : ItemStack = ItemStack.EMPTY.copy()

    override fun getId() = identifier

    override fun getSerializer(): RecipeSerializer<*>  = OneironautRecipeSerializer.INFUSE

    override fun getType(): RecipeType<*> = OneironautRecipeTypes.INFUSION_TYPE

    class Serializer : RecipeSerializerBase<InfusionRecipe>() {
        override fun fromJson(recipeID: ResourceLocation, json: JsonObject): InfusionRecipe {
            val blockIn = StateIngredientHelper.deserialize(GsonHelper.getAsJsonObject(json, "blockIn"))
            val result = StateIngredientHelper.readBlockState(GsonHelper.getAsJsonObject(json, "resultType"))
            val cost = GsonHelper.getAsLong(json, "mediaCost")
            return InfusionRecipe(recipeID, blockIn, result, cost)
        }

        override fun toNetwork(buf: FriendlyByteBuf, recipe: InfusionRecipe) {
            recipe.blockIn.write(buf)
            buf.writeVarInt(Block.getId(recipe.blockOut))
            buf.writeLong(recipe.mediaCost)
        }

        override fun fromNetwork(recipeID: ResourceLocation, buf: FriendlyByteBuf): InfusionRecipe {
            val blockIn = StateIngredientHelper.read(buf)
            val result = Block.stateById(buf.readVarInt())
            val cost = buf.readLong()
            return InfusionRecipe(recipeID, blockIn, result, cost)
        }
    }
}