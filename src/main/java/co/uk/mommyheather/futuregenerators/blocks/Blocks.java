package co.uk.mommyheather.futuregenerators.blocks;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Blocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FutureGenerators.MODID);

    public static final RegistryObject<Block> turbine = BLOCKS.register("turbine", () -> new BlockTurbine(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> lightningGenerator = BLOCKS.register("lightning_generator", () -> new BlockLightningGenerator(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> lightningDynamo = BLOCKS.register("lightning_dynamo", () -> new BlockLightningDynamo(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));


}
