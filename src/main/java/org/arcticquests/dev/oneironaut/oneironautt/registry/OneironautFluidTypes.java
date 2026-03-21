package org.arcticquests.dev.oneironaut.oneironautt.registry;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class OneironautFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Oneironaut.MODID);

    public static final RegistryObject<FluidType> THOUGHT_SLURRY_TYPE =
            FLUID_TYPES.register("thought_slurry", () -> new FluidType(
                    FluidType.Properties.create().descriptionId("fluid.oneironaut.thought_slurry").canExtinguish(true)
            ) {
                @Override
                public void initializeClient(java.util.function.Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return ResourceLocation.parse("oneironaut:block/thought_slurry");
                        }
                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return ResourceLocation.parse("oneironaut:block/thought_slurry_flowing");
                        }
                        @Override
                        public ResourceLocation getOverlayTexture() {
                            return ResourceLocation.parse("minecraft:block/water_overlay");
                        }

                        @Override
                        public int getTintColor() {
                            return 0xFFAD73CE;
                        }

                        @Override
                        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                            float fogStart = 10.0F;
                            float fogEnd = 12.0F;
                            RenderSystem.setShaderFogStart(fogStart);
                            RenderSystem.setShaderFogEnd(fogEnd);
                        }

                        public static final Vector3f fogColor = new Vector3f(0.678F, 0.451F, 0.808F);

                        @Override
                        public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                            return fogColor;
                        }
                    });
                }
            });

    public static void init(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }

}