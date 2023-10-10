package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TurbineScreen extends AbstractContainerScreen<TurbineMenu> {
    
    private static final int ENERGY_LEFT = 96;
    private static final int ENERGY_WIDTH = 72;
    private static final int ENERGY_TOP = 8;
    private static final int ENERGY_HEIGHT = 8;


    private final ResourceLocation GUI = new ResourceLocation(FutureGenerators.MODID, "textures/gui/processor.png");

    public TurbineScreen(TurbineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.inventoryLabelY = this.imageHeight - 110;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        int power = menu.power;
        int p = (int) ((power / (float) menu.powerMax) * ENERGY_WIDTH);
        graphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000, 0xff000000);
        graphics.fill(leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xff330000);
    }

    @Override
    public void render(GuiGraphics graphics, int mousex, int mousey, float partialTick) {
        super.render(graphics, mousex, mousey, partialTick);
        // Render tooltip with power if in the energy box
        if (mousex >= leftPos + ENERGY_LEFT && mousex < leftPos + ENERGY_LEFT + ENERGY_WIDTH && mousey >= topPos + ENERGY_TOP && mousey < topPos + ENERGY_TOP + ENERGY_HEIGHT) {
            int power = menu.power;
            graphics.renderTooltip(this.font, Component.literal(power + " RF"), mousex, mousey);
        }
    }

    
}
