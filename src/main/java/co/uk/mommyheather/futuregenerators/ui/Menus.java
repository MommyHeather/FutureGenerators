package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Menus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FutureGenerators.MODID);

    public static final RegistryObject<MenuType<TurbineMenu>> turbine = MENUS.register("turbine", () -> {
        return IForgeMenuType.create((window, inv, data) -> new TurbineMenu(window, inv.player, data.readBlockPos()));
    });

    public static final RegistryObject<MenuType<LightningGeneratorMenu>> lightningGenerator = MENUS.register("lightning_generator", () -> {
        return IForgeMenuType.create((window, inv, data) -> new LightningGeneratorMenu(window, inv.player, data.readBlockPos()));
    });

    public static final RegistryObject<MenuType<LightningDynamoMenu>> lightningDynamo = MENUS.register("lightning_dynamo", () -> {
        return IForgeMenuType.create((window, inv, data) -> new LightningDynamoMenu(window, inv.player, data.readBlockPos()));
    });

}
