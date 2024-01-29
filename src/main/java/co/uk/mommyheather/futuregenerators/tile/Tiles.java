package co.uk.mommyheather.futuregenerators.tile;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Tiles {
    
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FutureGenerators.MODID);


    public static final RegistryObject<BlockEntityType<TileTurbine>> turbine = TILES.register("turbine", () -> BlockEntityType.Builder.of(TileTurbine::new, Blocks.turbine.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileLightningGenerator>> lightningGenerator = TILES.register("lightning_generator", () -> BlockEntityType.Builder.of(TileLightningGenerator::new, Blocks.lightningGenerator.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileLightningDynamo>> lightningDynamo = TILES.register("lightning_dynamo", () -> BlockEntityType.Builder.of(TileLightningDynamo::new, Blocks.lightningDynamo.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileWasher>> washer = TILES.register("washer", () -> BlockEntityType.Builder.of(TileWasher::new, Blocks.washer.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileBoiler>> boiler = TILES.register("boiler", () -> BlockEntityType.Builder.of(TileBoiler::new, Blocks.boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileFluidTank>> fluidTank = TILES.register("tank", () -> BlockEntityType.Builder.of(TileFluidTank::new, Blocks.fluidTank.get()).build(null));
    public static final RegistryObject<BlockEntityType<TilePump>> pump = TILES.register("pump", () -> BlockEntityType.Builder.of(TilePump::new, Blocks.pump.get()).build(null));

}
