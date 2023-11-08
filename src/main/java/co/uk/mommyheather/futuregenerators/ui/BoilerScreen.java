package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BoilerScreen extends AbstractContainerScreen<BoilerMenu> {
    
    private static final int HOT_WATER_LEFT = 136;//142;
    private static final int HOT_WATER_WIDTH = 14;//24;
    private static final int HOT_WATER_TOP = 24;//20;
    private static final int HOT_WATER_HEIGHT = 37;//60;

    private static final int WATER_LEFT = 26;//10;
    private static final int WATER_WIDTH = 14;//24;
    private static final int WATER_TOP = 24;//20;
    private static final int WATER_HEIGHT = 37;//60;



    private final ResourceLocation GUI = new ResourceLocation(FutureGenerators.MODID, "textures/gui/boiler.png");

    public BoilerScreen(BoilerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
       // this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 105;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBackground(graphics);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        int water = menu.water;
        int hotWater = menu.hotWater;
        int w = WATER_HEIGHT - (int) ((water / (float) menu.waterMax) * WATER_HEIGHT);
        int h = HOT_WATER_HEIGHT - (int) ((hotWater / (float) (menu.waterMax * 2)) * HOT_WATER_HEIGHT);

        graphics.blit(GUI, leftPos + HOT_WATER_LEFT, topPos + HOT_WATER_TOP + h, 176, h+14, HOT_WATER_WIDTH, HOT_WATER_HEIGHT - h);
        graphics.blit(GUI, leftPos + WATER_LEFT, topPos + WATER_TOP + w, 176, w+51, WATER_WIDTH, WATER_HEIGHT - w);

        if (menu.timeRemaining > 0) {
            graphics.blit(GUI, leftPos + 81, topPos + 36, 176, 0, 14, 14);
        }


        Component burning = Component.translatable("futuregenerators.ui.burn_time", String.format("%,d", menu.timeRemaining));
        

        graphics.drawCenteredString(font, burning, this.width / 2, topPos + HOT_WATER_TOP + HOT_WATER_HEIGHT + font.lineHeight + 1, 16777215);

    }

    @Override
    public void render(GuiGraphics graphics, int mousex, int mousey, float partialTick) {
        super.render(graphics, mousex, mousey, partialTick);
        // Render tooltip with power if in the hot_water box
        if (mousex >= leftPos + HOT_WATER_LEFT && mousex < leftPos + HOT_WATER_LEFT + HOT_WATER_WIDTH && mousey >= topPos + HOT_WATER_TOP && mousey < topPos + HOT_WATER_TOP + HOT_WATER_HEIGHT) {
            int hotWater = menu.hotWater;
            graphics.renderTooltip(this.font, Component.literal(hotWater + " mb"), mousex, mousey);
        }
        else if (mousex >= leftPos + WATER_LEFT && mousex < leftPos + WATER_LEFT + WATER_WIDTH && mousey >= topPos + WATER_TOP && mousey < topPos + WATER_TOP + WATER_HEIGHT) {
            int water = menu.water;
            graphics.renderTooltip(this.font, Component.literal(water + " mb"), mousex, mousey);
        }
        else renderTooltip(graphics, mousex, mousey);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        //graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
    
}
