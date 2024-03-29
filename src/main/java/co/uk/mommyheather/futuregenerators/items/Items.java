package co.uk.mommyheather.futuregenerators.items;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.fluids.Fluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class Items {

    public static final ResourceKey<CreativeModeTab> CREATIVE = ResourceKey.create(Registries.CREATIVE_MODE_TAB, 
        new ResourceLocation(FutureGenerators.MODID, "creative_tab"));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FutureGenerators.MODID);

    
    public static final RegistryObject<BlockItem> turbine = ITEMS.register("turbine", () -> new BlockItem(Blocks.turbine.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> turbineController = ITEMS.register("turbine_controller", () -> new BlockItem(Blocks.turbineController.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> turbineCasing = ITEMS.register("turbine_casing", () -> new BlockItem(Blocks.turbineCasing.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> lightningGenerator = ITEMS.register("lightning_generator", () -> new BlockItem(Blocks.lightningGenerator.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> lightningDynamo = ITEMS.register("lightning_dynamo", () -> new BlockItem(Blocks.lightningDynamo.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> washer = ITEMS.register("washer", () -> new BlockItem(Blocks.washer.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> boiler = ITEMS.register("boiler", () -> new BlockItem(Blocks.boiler.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> fluidTank = ITEMS.register("tank", () -> new BlockItem(Blocks.fluidTank.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> pump = ITEMS.register("pump", () -> new BlockItem(Blocks.pump.get(), new Properties().stacksTo(64)));
    public static final RegistryObject<BlockItem> fluidPipe = ITEMS.register("fluid_pipe", () -> new BlockItem(Blocks.fluidPipe.get(), new Properties().stacksTo(64)));

    public static final RegistryObject<Item> lightningCharge = ITEMS.register("lightning_charge", () -> new Item(new Properties().stacksTo(1).durability(250)));
    

    public static void onCreativeModeTabRegister(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            helper.register(CREATIVE, CreativeModeTab.builder().icon(() -> new ItemStack(turbine.get()))
                .title(Component.translatable("futuregenerators.creativetab"))
                .displayItems((params, output) -> {
                    output.accept(new ItemStack(turbine.get()));
                    output.accept(new ItemStack(turbineController.get()));
                    output.accept(new ItemStack(turbineCasing.get()));
                    output.accept(new ItemStack(lightningGenerator.get()));
                    output.accept(new ItemStack(lightningDynamo.get()));
                    output.accept(new ItemStack(washer.get()));
                    output.accept(new ItemStack(boiler.get()));
                    output.accept(new ItemStack(fluidTank.get()));
                    output.accept(new ItemStack(pump.get()));
                    output.accept(new ItemStack(fluidPipe.get()));

                    output.accept(new ItemStack(lightningCharge.get()));

                    output.accept(new ItemStack(Fluids.HOT_WATER_BUCKET.get()));
                })
                .build());

        });
    }
}
