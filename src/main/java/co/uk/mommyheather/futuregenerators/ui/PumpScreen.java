package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.render.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class PumpScreen extends AbstractContainerScreen<PumpMenu> {
    
    private static final int FLUID_LEFT = 67;
    private static final int FLUID_WIDTH = 83;
    private static final int FLUID_TOP = 24;
    private static final int FLUID_HEIGHT = 45;

    private static final int ENERGY_LEFT = 10;
    private static final int ENERGY_WIDTH = 12;
    private static final int ENERGY_TOP = 28;
    private static final int ENERGY_HEIGHT = 37;


    private final ResourceLocation GUI = new ResourceLocation(FutureGenerators.MODID, "textures/gui/pump.png");

    public PumpScreen(PumpMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        //this.imageHeight = 190;
        this.inventoryLabelY = this.imageHeight - 105;
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBackground(graphics);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);


        if (!menu.pump.tank.isEmpty()) {
            float percent = (float) menu.pump.tank.getFluidAmount() / (float) FutureGeneratorsConfig.SERVER.fluidPumpCapacity.get();
            int ylevel = (int) (FLUID_HEIGHT * percent);

            RenderHelper.renderFluidInGui(graphics, leftPos + FLUID_LEFT, topPos + FLUID_TOP + (FLUID_HEIGHT - ylevel), FLUID_WIDTH, ylevel, menu.pump.tank.getFluid().getFluid());
        }


        //this goes after the fluid
        graphics.blit(GUI, leftPos + FLUID_LEFT, topPos + FLUID_TOP, 176, 0, 66, FLUID_HEIGHT);

        int p = ENERGY_HEIGHT - (int) ((menu.pump.battery.getEnergyStored() / (float) FutureGeneratorsConfig.SERVER.fluidPumpBattery.get()) * ENERGY_HEIGHT);

        graphics.blit(GUI, leftPos + ENERGY_LEFT, topPos + ENERGY_TOP + p, 176, p+46, ENERGY_WIDTH, ENERGY_HEIGHT - p);


    }

    @Override
    public void render(GuiGraphics graphics, int mousex, int mousey, float partialTick) {
        super.render(graphics, mousex, mousey, partialTick);

        FluidStack fluid = menu.pump.tank.getFluid();

        if (mousex >= leftPos + FLUID_LEFT && mousex < leftPos + FLUID_LEFT + FLUID_WIDTH && mousey >= topPos + FLUID_TOP && mousey < topPos + FLUID_TOP + FLUID_HEIGHT) {
            Component text;
            if (fluid.isEmpty()) {
                text = Component.literal(I18n.get("futuregenerators.ui.empty"));
            }
            else {
                text = Component.literal(I18n.get(fluid.getTranslationKey()) + ": " + (float) fluid.getAmount() / 1000F + " / " + (float) FutureGeneratorsConfig.SERVER.fluidPumpCapacity.get() / 1000F + "B");
            }
            graphics.renderTooltip(this.font, text, mousex, mousey);
        }
        else if (mousex >= leftPos + ENERGY_LEFT && mousex < leftPos + ENERGY_LEFT + ENERGY_WIDTH && mousey >= topPos + ENERGY_TOP && mousey < topPos + ENERGY_TOP + ENERGY_HEIGHT) {
            int power = menu.pump.battery.getEnergyStored();
            graphics.renderTooltip(this.font, Component.literal(power + " FE"), mousex, mousey);
        }
        else renderTooltip(graphics, mousex, mousey);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        if (!menu.pump.lastPumped.equals(Fluids.EMPTY)) {
            FluidStack fluid = new FluidStack(menu.pump.lastPumped, 1);
            Component pumping = Component.translatable("futuregenerators.ui.pumping", I18n.get(fluid.getTranslationKey()));
            graphics.drawString(this.font, pumping, this.imageWidth - this.font.width(pumping) - 5, this.titleLabelY, 4210752, false);
        }
        else {
            Component pumping = Component.translatable("futuregenerators.ui.pumping_nothing");
            graphics.drawString(this.font, pumping, this.imageWidth - this.font.width(pumping) - this.inventoryLabelX, this.titleLabelY, 4210752, false);
        }
        
        //graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
    
}
