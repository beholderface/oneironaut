package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.arcticquests.dev.oneironaut.oneironautt.casting.OvercastDamageEnchant;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;

import java.util.HashMap;

public class RenderBerryBushBlock extends BushBlock implements BonemealableBlock {
    public RenderBerryBushBlock(Properties settings) {
        super(settings);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0).setValue(THOUGHTS, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
        builder.add(THOUGHTS);
    }

    public static IntegerProperty AGE = SweetBerryBushBlock.AGE;
    public static final IntegerProperty THOUGHTS;
    private static final VoxelShape SMALL_SHAPE;
    private static final VoxelShape LARGE_SHAPE;

    private static final HashMap<Entity, Long> FEED_TIMESTAMP_MAP = new HashMap<>();

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {

    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        int thoughts = state.getValue(THOUGHTS);
        //small chance to grow on its own if there are non-brainswept mobs or players nearby
        boolean foundThinker = false;
        for (Entity entity : world.getEntities(null, new AABB(pos).inflate(8, 3, 8))){
            if (entity instanceof Mob mob){
                foundThinker = !IXplatAbstractions.INSTANCE.isBrainswept(mob);
            } else if (entity instanceof Player player && !player.isSpectator()){
                foundThinker = true;
            }
            if (foundThinker){
                break;
            }
        }
        //very small chance to grow from just slurry
        boolean foundSlurry = false;
        //same cuboid as if the block below it was farmland getting hydrated
        for (BlockState state2 : world.getBlockStates(new AABB(pos).inflate(4, 2, 4)).toList()){
            if (state2.is(OneironautTags.Blocks.growsMonkfruit)){
                foundSlurry = true;
                break;
            }
        }
        int chance = Integer.MAX_VALUE;
        if (foundSlurry && foundThinker){
            chance = 4;
        } else if (foundThinker){
            chance = 5;
        } else if (foundSlurry){
            chance = 20;
        }
        if (age < 3 && random.nextInt(chance) == 0 && chance != Integer.MAX_VALUE) {
            BlockState blockState;
            if (thoughts == 3){
                blockState = state.setValue(AGE, age + 1).setValue(THOUGHTS, 0);
            } else {
                blockState = state.setValue(THOUGHTS, thoughts + 1);
            }
            world.setBlock(pos, blockState, 2);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.125f, 1.0f);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity livingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE) {
            //I don't know what these numbers mean I'm just stealing this stuff from sweet berries
            entity.makeStuckInBlock(state, new Vec3(0.800000011920929, 0.75, 0.800000011920929));
            if (!world.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                double d = Math.abs(entity.getX() - entity.xOld);
                double e = Math.abs(entity.getZ() - entity.zOld);
                if (
                        (d >= 0.003000000026077032 || e >= 0.003000000026077032)
                        && world.getGameTime() >= FEED_TIMESTAMP_MAP.getOrDefault(entity, Long.MIN_VALUE) + 10
                ) {
                    FEED_TIMESTAMP_MAP.put(entity, world.getGameTime());
                    this.feed(state, world, pos, livingEntity);
                }
            }
        }
    }

    public void feed(BlockState state, Level world, BlockPos pos, LivingEntity target){
        boolean brainswept = false;
        RandomSource rand = world.random;
        int age = state.getValue(AGE);
        if (target instanceof Mob mob){
            brainswept = IXplatAbstractions.INSTANCE.isBrainswept(mob);
        }
        //the bush's tendrils coil in response to thought
        if (!brainswept){
            DamageSource berryDamage = target.damageSources().sweetBerryBush();
            target.hurt(berryDamage, target.isAlwaysTicking() ? 0.001f : 0f);
            OvercastDamageEnchant.applyMindDamage(null, target, 2,
                    target.getType().is(OneironautTags.Entities.mindRenderAutospare));
            if (target.getType().is(OneironautTags.Entities.monkfruitBlacklist)){
                return;
            }
            //did that damage flay the target?
            if (target instanceof Mob mob){
                brainswept = IXplatAbstractions.INSTANCE.isBrainswept(mob);
                if (brainswept){
                    //grow a full stage immediately
                    world.setBlockAndUpdate(pos, state.setValue(THOUGHTS, 0).setValue(AGE, Math.min(age + 1, 3)));
                    return;
                }
            }
            //make it respond less to players so that it's less trivial to just slap yourself with regen 0 and get tons of fruit
            int chance = target instanceof Player ? 9 : 3;
            //chance to grow by a portion of a stage
            if (rand.nextIntBetweenInclusive(1, chance) == chance && age < 3){
                if (state.getValue(THOUGHTS) < 3){
                    world.setBlockAndUpdate(pos, state.setValue(THOUGHTS, state.getValue(THOUGHTS) + 1));
                } else {
                    world.setBlockAndUpdate(pos, state.setValue(THOUGHTS, 0).setValue(AGE, age + 1));
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack clickStack = player.getItemInHand(hand);
        int age = state.getValue(AGE);
        boolean fullGrown = age == 3;
        int dropCount = 1 + world.random.nextInt(2);
        if (age > 1) {
            if (world.random.nextInt(3) == 3){
                OvercastDamageEnchant.applyMindDamage(null, player, 1, false);
            }
            boolean sheared = false;
            if (fullGrown && clickStack.getItem() == Items.SHEARS){
                popResource(world, pos, new ItemStack(OneironautItemRegistry.RENDER_THORNS.get(), dropCount));
                clickStack.hurtAndBreak(1, player, (playerx) ->
                    playerx.broadcastBreakEvent(hand));
                sheared = true;
            } else {
                popResource(world, pos, new ItemStack(OneironautItemRegistry.MONKFRUIT.get(), dropCount + (fullGrown ? 1 : 0)));
            }
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            BlockState blockState = state.setValue(AGE, sheared ? 0 : 1).setValue(THOUGHTS, 0);
            world.setBlock(pos, blockState, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockState));
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.use(state, world, pos, player, hand, hit);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if ((Integer)state.getValue(AGE) == 0) {
            return SMALL_SHAPE;
        } else {
            return (Integer)state.getValue(AGE) < 3 ? LARGE_SHAPE : super.getShape(state, world, pos, context);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return (Integer)state.getValue(AGE) < 3;
    }

    static {
        AGE = BlockStateProperties.AGE_3;
        THOUGHTS = IntegerProperty.create("thoughts", 0, 3);
        SMALL_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
        LARGE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    }
}
