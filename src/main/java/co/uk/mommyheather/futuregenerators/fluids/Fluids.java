package co.uk.mommyheather.futuregenerators.fluids;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.items.Items;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Fluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, FutureGenerators.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, FutureGenerators.MODID);

    private static ForgeFlowingFluid.Properties fluidProperties()
    {
        return new ForgeFlowingFluid.Properties(HOT_WATER_TYPE, HOT_WATER, HOT_WATER_FLOWING)
                .block(HOT_WATER_BLOCK)
                .bucket(HOT_WATER_BUCKET);
    }

    public static final RegistryObject<FluidType> HOT_WATER_TYPE = FLUID_TYPES.register("hot_water", () ->
        new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(false))
    {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
        {
            consumer.accept(new IClientFluidTypeExtensions()
            {
                private static final ResourceLocation STILL = new ResourceLocation("futuregenerators:block/hot_water"),
                        FLOW = new ResourceLocation("futuregenerators:block/hot_water"),
                        OVERLAY = new ResourceLocation("futuregenerators:block/hot_water"),
                        VIEW_OVERLAY = new ResourceLocation("futuregenerators:textures/block/hot_water.png");

                @Override
                public ResourceLocation getStillTexture()
                {
                    return STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture()
                {
                    return FLOW;
                }

                @Override
                public ResourceLocation getOverlayTexture()
                {
                    return OVERLAY;
                }

                @Override
                public ResourceLocation getRenderOverlayTexture(Minecraft mc)
                {
                    return VIEW_OVERLAY;
                }

                @Override
                public int getTintColor()
                {
                    return 0xAF7FFFD4;
                }

                @Override
                public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
                {
                    int color = this.getTintColor();
                    return new Vector3f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F);
                }

                @Override
                public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
                {
                    nearDistance = -8F;
                    farDistance = 24F;

                    if (farDistance > renderDistance)
                    {
                        farDistance = renderDistance;
                        shape = FogShape.CYLINDER;
                    }

                    RenderSystem.setShaderFogStart(nearDistance);
                    RenderSystem.setShaderFogEnd(farDistance);
                    RenderSystem.setShaderFogShape(shape);
                }
            });
        }
    });
    

    public static final RegistryObject<FlowingFluid> HOT_WATER = FLUIDS.register("hot_water", () ->
        new ForgeFlowingFluid.Source(fluidProperties()));
    public static final RegistryObject<Fluid> HOT_WATER_FLOWING = FLUIDS.register("hot_water_flowing", () ->
        new ForgeFlowingFluid.Flowing(fluidProperties()));
    public static final RegistryObject<LiquidBlock> HOT_WATER_BLOCK = Blocks.BLOCKS.register("hot_water", () ->
        new LiquidBlock(HOT_WATER, BlockBehaviour.Properties.of().noCollission().replaceable().strength(100.0F).noLootTable()));
    public static final RegistryObject<Item> HOT_WATER_BUCKET = Items.ITEMS.register("hot_water_bucket", () ->
        new BucketItem(HOT_WATER, new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)));

}