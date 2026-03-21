package org.arcticquests.dev.oneironaut.oneironautt

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapLocationInWrongDimension
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.forge.xplat.ForgeXplatImpl
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.GoalSelector
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.npc.VillagerDataHolder
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.border.WorldBorder
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifier
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifierManager
import org.arcticquests.dev.oneironaut.oneironautt.casting.environments.ReverbRodCastEnv
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaKeyable
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.DimIota
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.SoulprintIota
import org.arcticquests.dev.oneironaut.oneironautt.mixin.GeneralCastEnvInvoker
import org.arcticquests.dev.oneironaut.oneironautt.mixin.IotaTypeInvoker
import org.arcticquests.dev.oneironaut.oneironautt.network.OneironautNetwork
import org.arcticquests.dev.oneironaut.oneironautt.network.UnBrainsweepPacket
import org.arcticquests.dev.oneironaut.oneironautt.recipe.OneironautRecipeTypes
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.random.Random

@Suppress("forRemoval")
fun List<Iota>.getDimIota(idx: Int, argc: Int = 0): DimIota {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is DimIota) {
        return x
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:imprint")
}

fun List<Iota>.getDimension(idx: Int, argc: Int = 0, server : MinecraftServer): ServerLevel {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is DimIota) {
        val world = x.toWorld(server)
        assert(world != null)
        return world
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:imprint")
}

fun ServerLevel.assertTeleportationAllowed(){
    val worldKey = this.dimension()
    if (!HexConfig.server().canTeleportInThisDimension(worldKey)){
        throw MishapLocationInWrongDimension(worldKey.location())
    }
}

fun List<Iota>.getSoulprint(idx: Int, argc: Int = 0) : UUID {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is SoulprintIota) {
        return x.entity
    }

    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:soulprint")
}

fun ResourceLocation.getBlockTagKey() : TagKey<Block>{
    return TagKey.create(Registries.BLOCK, this)
}
fun String.getBlockTagKey() : TagKey<Block>{
    return ResourceLocation.parse(this).getBlockTagKey()
}
fun ResourceLocation.getEntityTagKey() : TagKey<EntityType<*>>{
    return TagKey.create(Registries.ENTITY_TYPE, this)
}
fun String.getEntityTagKey() : TagKey<EntityType<*>>{
    return ResourceLocation.parse(this).getEntityTagKey()
}
fun ResourceLocation.getItemTagKey() : TagKey<Item> {
    return TagKey.create(Registries.ITEM, this)
}
fun String.getItemTagKey() : TagKey<Item>{
    return ResourceLocation.parse(this).getItemTagKey()
}


fun getInfuseResult(targetState: BlockState, world: Level) : Triple<BlockState, Long, String?> {
    var conversionResult : Triple<BlockState, Long, String?> = when(targetState.block){
        Blocks.WITHER_ROSE -> {
            val smallflowers = arrayOf(
                Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY)
            val flowerIndex = Random.nextInt(0, smallflowers.size)
            Triple(smallflowers[flowerIndex].defaultBlockState(), 5, null)
        }
        else -> Triple(Blocks.BARRIER.defaultBlockState(), -1, null)
    }
    val debugMessages = false
    if (conversionResult.second == -1L){
        Oneironaut.boolLogger(
            "did not find a hard-coded conversion",
            debugMessages
        )
        val recipeManager : RecipeManager = world.recipeManager
        val infusionRecipes = recipeManager.getAllRecipesFor(OneironautRecipeTypes.INFUSION_TYPE)
        val recipe = infusionRecipes.find { it.matches(targetState) }
        if (recipe != null){
            Oneironaut.boolLogger(
                "found a matching recipe, ${recipe.blockIn} to ${recipe.blockOut.block.name.string}",
                debugMessages
            )
            /*val advancement = recipe.advancement
            val passedAdvancement : String? = if (advancement.equals("")){
                null
            } else {
                advancement
            }*/
            conversionResult = Triple(recipe.blockOut, recipe.mediaCost, null)
        } else {
            Oneironaut.boolLogger(
                "no matching recipe found",
                debugMessages
            )
        }
    } else {
        Oneironaut.boolLogger(
            "found a hard-coded conversion",
            debugMessages
        )
    }
    return Triple(preserveStates(targetState, conversionResult.first), conversionResult.second, conversionResult.third)
}
fun preserveStates(oldState : BlockState, desiredState : BlockState) : BlockState {
    val debugmessages = false
    var newState = desiredState
    if (desiredState != Blocks.BARRIER.defaultBlockState()){
        val boolsToKeep : List<Property<Boolean>> = listOf(BlockStateProperties.WATERLOGGED, BlockStateProperties.HANGING)
        for (property in boolsToKeep){
            if (oldState.hasProperty(property)){
                val value = oldState.getValue(property)
                Oneironaut.boolLogger(
                    "property ${property.name} has value $value",
                    debugmessages
                )
                newState = newState.setValue(property, value)
            }
        }
        val intsToKeep : List<Property<Int>> = listOf(BlockStateProperties.ROTATION_16)
        for (property in intsToKeep){
            if (oldState.hasProperty(property)){
                val value = oldState.getValue(property)
                Oneironaut.boolLogger(
                    "property ${property.name} has value $value",
                    debugmessages
                )
                newState = newState.setValue(property, value)
            }
        }
        val dirsToKeep : List<Property<Direction>> = listOf(BlockStateProperties.FACING, BlockStateProperties.HORIZONTAL_FACING)
        for (property in dirsToKeep) {
            if (oldState.hasProperty(property)) {
                val value = oldState.getValue(property)
                Oneironaut.boolLogger(
                    "property ${property.name} has value $value",
                    debugmessages
                )
                newState = newState.setValue(property, value)
            }
        }
    }
    return newState
}

fun isUnsafe(world: ServerLevel, pos: BlockPos, up: Boolean) : Boolean{
    val state = world.getBlockState(pos)
    var output = when (state.block){
        Blocks.LAVA -> true
        Blocks.FIRE -> true
        Blocks.SOUL_FIRE -> true
        Blocks.CAMPFIRE -> true
        Blocks.SOUL_CAMPFIRE -> true
        Blocks.MAGMA_BLOCK -> true
        Blocks.CACTUS -> true
        Blocks.SCULK_SHRIEKER -> true
        else -> false
    }
    if (state.canOcclude() && up){
        output = true
    }
    return output
}
fun isSolid(world: ServerLevel, pos: BlockPos) : Boolean{
    var output = false
    val state = world.getBlockState(pos)
    if (state.fluidState.isEmpty && !state.isAir && !state.block.isPossibleToRespawnInThis(state)){
        output = true
    } else if (state.block.defaultBlockState().properties.contains(BlockStateProperties.WATERLOGGED) && !state.isAir){
        if (state.block.defaultBlockState().getValue(BlockStateProperties.WATERLOGGED) == true){
            output = true
        }
    }

    /*if (state.isTranslucent(world.getChunkAsView(floor(pos.x / 16.0).toInt(), floor(pos.z / 16.0).toInt()), pos)){
        output = true
    }*/
    return output
}

fun stringToWorld(key : String, server : MinecraftServer) : ServerLevel?{
    val regKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(key))
    return server.getLevel(regKey)
}

fun playerUUIDtoServerPlayer(uuid: UUID, server: MinecraftServer): ServerPlayer? {
    //val server = player.server
    return server.playerList?.getPlayer(uuid)
}

fun Vec3.toVec3i() : Vec3i {
    return Vec3i(floor(this.x).toInt(), floor(this.y).toInt(), floor(this.z).toInt())
}

fun Vec3.toBlockPos() : BlockPos {
    return BlockPos(floor(this.x).toInt(), floor(this.y).toInt(), floor(this.z).toInt())
}

fun genCircle(world : WorldGenLevel, center : BlockPos, diameter : Int, state : BlockState, replacable : Array<Block>, fillPortion : Double) : Int{
    val realCenter = Vec3(center.x + 0.5, center.y + 0.5, center.z + 0.5)
    //val area = diameter * diameter
    val radius = diameter.toDouble() / 2
    var offset = Vec3.ZERO
    val corner = realCenter.add(-(radius + 0.5), 0.0, -(radius + 0.5))
    var current = corner
    var placed = 0;
    for (x in 0 .. diameter){
        for (y in 0 .. diameter){
            offset = Vec3(x.toDouble(), 0.0, y.toDouble())
            current = corner.add(offset)
            if (world.random.nextIntBetweenInclusive(0, 999) / 10.0 <= fillPortion * 100)
            if (current.distanceTo(realCenter) <= radius && replacable.contains(world.getBlockState(BlockPos(current.toVec3i())).block)){
                world.setBlock(BlockPos(current.toVec3i()), state, 0b10)
                placed++
            }
        }
    }
    return placed
    /*for (i in 0 .. (area * 3)){

    }*/
}

fun isPlayerEnlightened(player : ServerPlayer) : Boolean {
    val adv = player.server?.advancements?.getAdvancement(HexAPI.modLoc("enlightenment"))
    val advs = player.advancements
    val enlightened : Boolean = if (advs.getOrStartProgress(adv) != null){
        advs.getOrStartProgress(adv).isDone
    } else {
        false
    }
    return enlightened;
}

fun isUsingRod(env : CastingEnvironment) : Boolean {
    return env is ReverbRodCastEnv
}

fun getPositionsInCuboid(corner1 : BlockPos, corner2 : BlockPos, pointsToExclude : List<BlockPos>) : List<BlockPos>{
    val cuboid = AABB(corner1, corner2)
    val lowerCorner = BlockPos(cuboid.minX.toInt(), cuboid.minY.toInt(), cuboid.minZ.toInt())
    val outputList : MutableList<BlockPos> = mutableListOf()
    var currentPos : BlockPos
    for (i in 0 .. cuboid.xsize.toInt()){
        for (j in 0 .. cuboid.ysize.toInt()){
            for (k in 0 .. cuboid.zsize.toInt()){
                currentPos = lowerCorner.offset(i, j, k)
                if (!pointsToExclude.contains(currentPos)){
                    outputList.add(currentPos)
                }
            }
        }
    }
    return outputList.toList()
}

fun getPositionsInCuboid(corner1 : BlockPos, corner2 : BlockPos, pointToExclude : BlockPos) : List<BlockPos>{
    return getPositionsInCuboid(corner1, corner2, listOf(pointToExclude))
}

fun getPositionsInCuboid(corner1 : BlockPos, corner2 : BlockPos) : List<BlockPos>{
    return getPositionsInCuboid(
        corner1,
        corner2,
        listOf(corner2.offset((corner1.x - corner2.x).absoluteValue + 20, 0, 0))
    )
}

fun AABB.corners() : List<Vec3>{
    return listOf(
        Vec3(this.minX, this.minY, this.minZ), Vec3(this.maxX, this.minY, this.minZ),
        Vec3(this.maxX, this.maxY, this.minZ), Vec3(this.maxX, this.maxY, this.maxZ),
        Vec3(this.minX, this.maxY, this.maxZ), Vec3(this.minX, this.minY, this.maxZ),
        Vec3(this.maxX, this.minY, this.maxZ), Vec3(this.minX, this.maxY, this.minZ)
    )
}

fun vecProximity(a: Vec3, b: Vec3): Double {
    //I'm not sure what the best way to do this is, but this way works for hexes so it's what I tried first
    return a.normalize().subtract(b.normalize()).length()
}

fun vecProximity(a: Direction, b: Vec3): Double {
    return vecProximity(Vec3.atLowerCornerOf(a.normal), b)
}
private const val DECORATIVE_KEY = "oneironaut:decorative"


var Entity.isDecorative: Boolean
    get() = this.persistentData.getBoolean(DECORATIVE_KEY)
    set(value) { this.persistentData.putBoolean(DECORATIVE_KEY, value) }



fun Mob.unbrainsweep() {
    assert(false)
    val patient = this
    if (!patient.level().isClientSide){
        Oneironaut.LOGGER.info("Attempting to unbrainsweep ${this.name} client-side")
        OneironautNetwork.CHANNEL.send(
            PacketDistributor.NEAR.with {
                PacketDistributor.TargetPoint(
                    patient.position().x,
                    patient.position().y,
                    patient.position().z,
                    256.0 * 256.0,
                    (patient.level() as ServerLevel).dimension()
                )
            },
            UnBrainsweepPacket(patient.id)
        )
    } else {
        Oneironaut.LOGGER.info("Attempting to unbrainsweep ${this.name} server-side")
    }
    patient.persistentData.putBoolean(ForgeXplatImpl.TAG_BRAINSWEPT, false)
    patient.isNoAi = false
    val brain = patient.brain
    patient.goalSelector.availableGoals.clear()
    brain.useDefaultActivity()
    brain.updateActivityFromSchedule(patient.level().dayTime, patient.level().gameTime)
    if (patient is VillagerDataHolder){
        val newData = patient.villagerData.setLevel(1).setProfession(VillagerProfession.NITWIT)
        patient.villagerData = newData
    }


    val refreshNBT = patient.saveWithoutId(CompoundTag())
    val forgeTag = refreshNBT.getCompound("ForgeData")
    forgeTag.putBoolean(ForgeXplatImpl.TAG_BRAINSWEPT, false)
    refreshNBT.put("ForgeData", forgeTag)
    patient.load(refreshNBT)
}


fun AABB.longestAxisLength() : Double{
    val x = this.xsize
    val y = this.ysize
    val z = this.zsize
    return if (x >= y && x >= z){
        x
    } else if (y >= x && y >= z){
        y
    } else {
        z
    }
}

fun AABB.intersectsPermissive(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
    return this.minX <= maxX && this.maxX >= minX && this.minY <= maxY && this.maxY >= minY && this.minZ <= maxZ && this.maxZ >= minZ
}

fun AABB.intersectsPermissive(box : AABB): Boolean {
    return this.intersectsPermissive(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)
}

fun AABB.containsPermissive(x : Double, y : Double, z : Double) : Boolean{
    return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ
}

fun AABB.containsPermissive(vec : Vec3) : Boolean{
    return this.containsPermissive(vec.x, vec.y, vec.z)
}

fun AABB.volume() : Double {
    return this.xsize * this.ysize * this.zsize
}

fun FrozenPigment.rawColor(time : Float, pos : Vec3){
    this.colorProvider.getColor(time, pos)
}

fun List<Iota>.getNonlivingIfAllowed(idx: Int, argc: Int = 0): Entity {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    val nonlivingAllowed = OneironautConfig.server.planeShiftNonliving
    if (x is EntityIota) {
        val e = x.entity
        if (nonlivingAllowed || (e is LivingEntity && e !is ArmorStand)){
            return e
        }
    }
    val stub = if (nonlivingAllowed){
        "entity"
    } else {
        "entity.living"
    }
    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), stub)
}

fun List<Iota>.getIdeaKey(idx : Int, argc: Int = 0, env : CastingEnvironment) : IdeaKeyable {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is IdeaKeyable){
        if (x.isValidKey(env)){
            return x
        }
    }
    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "oneironaut:invalidkey")
}

fun BlockPos.toUUID() : UUID{
    return UUID(0L, this.asLong())
}

fun UUID.toBlockPos() : BlockPos {
    return BlockPos.of(this.leastSignificantBits)
}

fun handleIncreasedStackLimit(env : CastingEnvironment, img : CastingImage, examinee : Iterable<Iota>, original : Operation<Boolean>) : Boolean {
    if (env.castingEntity is ServerPlayer) {
        val player = env.castingEntity as ServerPlayer
        val manager = ConceptModifierManager.getServerState(Oneironaut.getCachedServer())
        if (manager.hasModifierType(player, ConceptModifier.ModifierType.STACK_LIMIT)) {
            var totalSize = 0
            val modifiedMaximum = HexIotaTypes.MAX_SERIALIZATION_TOTAL * 2
            for (iota in examinee) {
                if (IotaTypeInvoker.`oneironaut$isTooLarge`(listOf(iota), 0)) {
                    img.userData.putBoolean(MiscStaticData.TAG_ALLOW_SERIALIZE, false)
                    return true
                }
                totalSize += iota.size()
            }
            if (totalSize > modifiedMaximum) { //still too large
                img.userData.putBoolean(MiscStaticData.TAG_ALLOW_SERIALIZE, false)
                return true
            } else if (totalSize > HexIotaTypes.MAX_SERIALIZATION_TOTAL) { //large enough to incur media costs (can still fail if there is not enough media)
                val remainingCost = (env as GeneralCastEnvInvoker).`oneironaut$extractMediaEnvironment`(
                    (totalSize - HexIotaTypes.MAX_SERIALIZATION_TOTAL).toLong(),
                    false
                )
                if (remainingCost <= 0) {
                    img.userData.putBoolean(MiscStaticData.TAG_ALLOW_SERIALIZE, true)
                    return false
                } else {
                    img.userData.putBoolean(MiscStaticData.TAG_ALLOW_SERIALIZE, false)
                    return true
                }
            }
            img.userData.putBoolean(MiscStaticData.TAG_ALLOW_SERIALIZE, true)
            return false
        }
    }
    val originalResult = original.call(examinee)
    img.userData.putBoolean(MiscStaticData.TAG_ALLOW_SERIALIZE, originalResult)
    return originalResult
}

fun DyeColor.toVec3d() : Vec3 {
    return Vec3(
        this.textureDiffuseColors[0].toDouble(),
        this.textureDiffuseColors[1].toDouble(),
        this.textureDiffuseColors[2].toDouble()
    )
}

fun colorToClosestPigment(color : Int) : FrozenPigment{
    //conversion code taken from vanilla DyeColor stuff
    val j = (color and 16711680) shr 16
    val k = (color and '\uff00'.code) shr 8
    val l = (color and 255) shr 0
    return colorToClosestPigment(Vec3(j.toDouble() / 255.0f, k.toDouble() / 255.0f, l.toDouble() / 255.0f))
}

fun colorToClosestPigment(color : Vec3) : FrozenPigment{
    var distance = Double.MAX_VALUE
    var dye = DyeColor.RED
    for (checked in DyeColor.values()){
        val current = checked.toVec3d().distanceTo(color)
        if (current < distance){
            distance = current
            dye = checked
        }
    }
    return FrozenPigment(HexItems.DYE_PIGMENTS[dye]!!.defaultInstance, Util.NIL_UUID)
}

fun Vec3.scaleBetweenDimensions(origin : Level, destination : Level) : Vec3 {
    val x = this.x
    val z = this.z
    val compression = origin.dimensionType().coordinateScale / destination.dimensionType().coordinateScale
    return Vec3(x * compression, this.y, z * compression)
}

fun Vec3.coerceWithinBorder(border: WorldBorder) : Vec3 {
    return Vec3(this.x.coerceIn(border.minX, border.maxX), this.y, this.z.coerceIn(border.minZ, border.maxZ))
}

fun Vec3.coerceWithinBorder(world: Level) : Vec3 {
    return this.coerceWithinBorder(world.worldBorder)
}

object MiscStaticData {
    const val TAG_ALLOW_SERIALIZE = "serializeAnyway"
}