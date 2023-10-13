package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public class LightningGeneratorScreen extends AbstractContainerScreen<LightningGeneratorMenu> {
    
    private static final int ENERGY_LEFT = 142;
    private static final int ENERGY_WIDTH = 24;
    private static final int ENERGY_TOP = 20;
    private static final int ENERGY_HEIGHT = 60;



    private final ResourceLocation GUI = new ResourceLocation(FutureGenerators.MODID, "textures/gui/background.png");
    private final ResourceLocation EXTRAS = new ResourceLocation(FutureGenerators.MODID, "textures/gui/extras.png");

    public LightningGeneratorScreen(LightningGeneratorMenu menu, Inventory inventory, Component title) {
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
        int p = ENERGY_HEIGHT - (int) ((power / (float) menu.powerMax) * ENERGY_HEIGHT);
        graphics.fill(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xff330000);
        graphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP + p, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000, 0xff000000);

        
        graphics.blit(EXTRAS, leftPos + 99, topPos + ((ENERGY_TOP + ENERGY_HEIGHT) / 2) - 16, 0, 60, 36, 54);

        Component rod = menu.hasRod ? Component.translatable("futuregenerators.ui.has_rod") : Component.translatable("futuregenerators.ui.no_rod")
            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16724539)));

        //Component producing = Component.translatable("futuregenerators.ui.production", (menu.speed * FutureGeneratorsConfig.SERVER.turbineFeRatio.get()));
        int height = topPos + ENERGY_TOP + 3;
        for (FormattedCharSequence line : font.split(rod, 90)) {
            graphics.drawString(font, line, this.leftPos + 5, height, 16777215);
            height += font.lineHeight;
        }

        Component dynamos = Component.translatable("futuregenerators.ui.dynamos", menu.dynamos);

        graphics.drawString(font, dynamos, this.leftPos + 5, height + 3, 16777215);
        //graphics.drawCenteredString(font, producing, this.width / 2, topPos + ENERGY_HEIGHT + ENERGY_TOP - 10, 16777215);

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

    
}
