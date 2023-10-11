package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import mekanism.common.block.basic.BlockFluidTank;
import mekanism.common.tile.TileEntityFluidTank;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.I18NParser;

public class TurbineScreen extends AbstractContainerScreen<TurbineMenu> {
    
    private static final int ENERGY_LEFT = 142;
    private static final int ENERGY_WIDTH = 24;
    private static final int ENERGY_TOP = 20;
    private static final int ENERGY_HEIGHT = 60;

    private static final int WATER_LEFT = 10;
    private static final int WATER_WIDTH = 24;
    private static final int WATER_TOP = 20;
    private static final int WATER_HEIGHT = 60;



    private final ResourceLocation GUI = new ResourceLocation(FutureGenerators.MODID, "textures/gui/background.png");
    private final ResourceLocation EXTRAS = new ResourceLocation(FutureGenerators.MODID, "textures/gui/extras.png");

    public TurbineScreen(TurbineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 105;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBackground(graphics);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        int power = menu.power;
        int water = menu.water;
        int p = ENERGY_HEIGHT - (int) ((power / (float) menu.powerMax) * ENERGY_HEIGHT);
        int w = WATER_HEIGHT - (int) ((water / (float) menu.waterMax) * WATER_HEIGHT);
        graphics.fill(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xff330000);
        graphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP + p, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000, 0xff000000);

        graphics.blit(EXTRAS, leftPos + WATER_LEFT, topPos + WATER_TOP, 0, 0, WATER_WIDTH, WATER_HEIGHT);
        graphics.blit(EXTRAS, leftPos + WATER_LEFT, topPos + WATER_TOP + w, 24, w, WATER_WIDTH, WATER_HEIGHT);

        Component speed = Component.translatable("futuregenerators.ui.speed", menu.speed);

        Component producing = Component.translatable("futuregenerators.ui.production", (menu.speed * FutureGeneratorsConfig.SERVER.turbineFeRatio.get()));
        

        graphics.drawCenteredString(font, speed, this.width / 2, topPos + ENERGY_TOP + 3, 16777215);
        graphics.drawCenteredString(font, producing, this.width / 2, topPos + ENERGY_HEIGHT + ENERGY_TOP - 10, 16777215);

    }

    @Override
    public void render(GuiGraphics graphics, int mousex, int mousey, float partialTick) {
        super.render(graphics, mousex, mousey, partialTick);
        // Render tooltip with power if in the energy box
        if (mousex >= leftPos + ENERGY_LEFT && mousex < leftPos + ENERGY_LEFT + ENERGY_WIDTH && mousey >= topPos + ENERGY_TOP && mousey < topPos + ENERGY_TOP + ENERGY_HEIGHT) {
            int power = menu.power;
            graphics.renderTooltip(this.font, Component.literal(power + " FE"), mousex, mousey);
        }
        if (mousex >= leftPos + WATER_LEFT && mousex < leftPos + WATER_LEFT + WATER_WIDTH && mousey >= topPos + WATER_TOP && mousey < topPos + WATER_TOP + WATER_HEIGHT) {
            int water = menu.water;
            graphics.renderTooltip(this.font, Component.literal(water + " mb"), mousex, mousey);
        }
    }

    
}
