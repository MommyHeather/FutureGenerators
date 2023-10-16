package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public class LightningGeneratorScreen extends AbstractContainerScreen<LightningGeneratorMenu> {
        
    private static final int ENERGY_LEFT = 136;//142;
    private static final int ENERGY_WIDTH = 12;//24;
    private static final int ENERGY_TOP = 24;//20;
    private static final int ENERGY_HEIGHT = 37;//60;



    private final ResourceLocation GUI = new ResourceLocation(FutureGenerators.MODID, "textures/gui/lightning_generator.png");
    public LightningGeneratorScreen(LightningGeneratorMenu menu, Inventory inventory, Component title) {
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

        int power = menu.power;
        int p = ENERGY_HEIGHT - (int) ((power / (float) menu.powerMax) * ENERGY_HEIGHT);

        graphics.blit(GUI, leftPos + ENERGY_LEFT, topPos + ENERGY_TOP + p, 176, p+14, ENERGY_WIDTH, ENERGY_HEIGHT - p);

        Component rod = menu.hasRod ? Component.translatable("futuregenerators.ui.has_rod") : Component.translatable("futuregenerators.ui.no_rod")
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16724539)));

        int height = topPos + ENERGY_TOP + 3;
        for (FormattedCharSequence line : font.split(rod, 90)) {
            graphics.drawString(font, line, this.leftPos + 5, height, 16777215);
            height += font.lineHeight;
        }

        Component dynamos = Component.translatable("futuregenerators.ui.dynamos", menu.dynamos);

        graphics.drawString(font, dynamos, this.leftPos + 5, height + 3, 16777215);
       
    }

    @Override
    public void render(GuiGraphics graphics, int mousex, int mousey, float partialTick) {
        super.render(graphics, mousex, mousey, partialTick);
        // Render tooltip with power if in the energy box
        if (mousex >= leftPos + ENERGY_LEFT && mousex < leftPos + ENERGY_LEFT + ENERGY_WIDTH && mousey >= topPos + ENERGY_TOP && mousey < topPos + ENERGY_TOP + ENERGY_HEIGHT) {
            int power = menu.power;
            graphics.renderTooltip(this.font, Component.literal(power + " FE"), mousex, mousey);
        }
        else {
            renderTooltip(graphics, mousex, mousey);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        //graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
    
}
