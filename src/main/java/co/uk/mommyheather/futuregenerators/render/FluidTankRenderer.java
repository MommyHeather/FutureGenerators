package co.uk.mommyheather.futuregenerators.render;

import com.mojang.blaze3d.vertex.PoseStack;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.tile.TileFluidTank;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class FluidTankRenderer implements BlockEntityRenderer<TileFluidTank> {

    public FluidTankRenderer(BlockEntityRendererProvider.Context context) {
        
    }
    

    @Override
    public void render(TileFluidTank tile, float partialTicks, PoseStack stack, MultiBufferSource bufferSource,
            int packedLightIn, int packedOverlayIn) {
                

        FluidStack fluid = tile.tank.getFluid();
        if (fluid.isEmpty() || fluid.getAmount() <= 0) return;

        float ylevel = ( (float) fluid.getAmount() / (float) FutureGeneratorsConfig.SERVER.fluidTankCapacity.get() );
        
        RenderHelper.renderFluidInWorld(tile.getBlockPos(), partialTicks, stack, bufferSource, packedLightIn, packedOverlayIn, fluid.getFluid(), ylevel);

    }
}