package co.uk.mommyheather.futuregenerators.items;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FutureGenerators.MODID);

    
    public static final RegistryObject<BlockItem> turbine = Items.ITEMS.register("turbine", () -> new BlockItem(Blocks.turbine.get(), new Properties().stacksTo(64)));

}
