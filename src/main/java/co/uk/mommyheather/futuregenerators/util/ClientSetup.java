package co.uk.mommyheather.futuregenerators.util;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.ui.BoilerScreen;
import co.uk.mommyheather.futuregenerators.ui.FluidTankScreen;
import co.uk.mommyheather.futuregenerators.ui.LightningDynamoScreen;
import co.uk.mommyheather.futuregenerators.ui.LightningGeneratorScreen;
import co.uk.mommyheather.futuregenerators.ui.Menus;
import co.uk.mommyheather.futuregenerators.ui.MultiblockTurbineScreen;
import co.uk.mommyheather.futuregenerators.ui.PumpScreen;
import co.uk.mommyheather.futuregenerators.ui.TurbineScreen;
import co.uk.mommyheather.futuregenerators.ui.WasherScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FutureGenerators.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Menus.turbine.get(), TurbineScreen::new);
            MenuScreens.register(Menus.multiblockTurbine.get(), MultiblockTurbineScreen::new);
            MenuScreens.register(Menus.lightningGenerator.get(), LightningGeneratorScreen::new);
            MenuScreens.register(Menus.lightningDynamo.get(), LightningDynamoScreen::new);
            MenuScreens.register(Menus.washer.get(), WasherScreen::new);
            MenuScreens.register(Menus.boiler.get(), BoilerScreen::new);
            MenuScreens.register(Menus.fluidTank.get(), FluidTankScreen::new);
            MenuScreens.register(Menus.pump.get(), PumpScreen::new);
        });
    }

}

