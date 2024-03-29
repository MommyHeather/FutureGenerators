package co.uk.mommyheather.futuregenerators;

import com.mojang.logging.LogUtils;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.fluids.Fluids;
import co.uk.mommyheather.futuregenerators.items.Items;
import co.uk.mommyheather.futuregenerators.render.FluidTankRenderer;
import co.uk.mommyheather.futuregenerators.render.PumpRenderer;
import co.uk.mommyheather.futuregenerators.tile.Tiles;
import co.uk.mommyheather.futuregenerators.ui.Menus;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;


import org.slf4j.Logger;

@Mod(FutureGenerators.MODID)
public class FutureGenerators
{

    public static final Logger LOGGER = LogUtils.getLogger();   
    public static final String MODID = "futuregenerators";

    public FutureGenerators()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        Fluids.FLUID_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Fluids.FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Tiles.TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Menus.MENUS.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(Items::onCreativeModeTabRegister);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterRenderers);

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        
        FutureGeneratorsConfig.loadConfig(FutureGeneratorsConfig.SERVER_SPEC, event.getServer().getWorldPath(new LevelResource("serverconfig")).resolve(MODID + ".toml").toString());
        
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        
    }

    
    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event){
        
    }

    public void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Tiles.fluidTank.get(), FluidTankRenderer::new);
        event.registerBlockEntityRenderer(Tiles.pump.get(), PumpRenderer::new);
    }

}
