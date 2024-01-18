package co.uk.mommyheather.futuregenerators.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class RenderHelper {

    private static final float MIN = 0.001F;
    private static final float MAX = 0.999F;

    public static void renderFluidInWorld(BlockPos blockPos, float partialTicks, PoseStack stack,
            MultiBufferSource bufferSource, int packedLightIn, int overlay, Fluid fluid, float ylevel) {

        
        //Push a pose here - if our pushes are equal to our pops, it should prevent any problems in this render from affecting other stuff.
        //Should.
        stack.pushPose();
        
        IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation texture = properties.getStillTexture();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);

        int[] col = splitRGBA(properties.getTintColor());

        VertexConsumer builder = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());
        Matrix4f matrix = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        float minU = sprite.getU(5);
        float maxU = sprite.getU(11);
        float minV = sprite.getV(5);
        float maxV = sprite.getV(11);

        ylevel = Math.min(ylevel, MAX);


        //Top
        builder.vertex(matrix, MAX, ylevel, MIN).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
        builder.vertex(matrix, MIN, ylevel, MIN).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
        builder.vertex(matrix, MIN, ylevel, MAX).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();
        builder.vertex(matrix, MAX, ylevel, MAX).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  1, 0).endVertex();

        //Bottom
        builder.vertex(matrix, MIN, MIN, MIN).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
        builder.vertex(matrix, MAX, MIN, MIN).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
        builder.vertex(matrix, MAX, MIN, MAX).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();
        builder.vertex(matrix, MIN, MIN, MAX).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0, -1, 0).endVertex();

        //North
        builder.vertex(matrix, MAX, MIN, MIN).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MIN, MIN, MIN).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MIN, ylevel, MIN).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();
        builder.vertex(matrix, MAX, ylevel, MIN).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0, -1).endVertex();

        //South
        builder.vertex(matrix, MIN, MIN, MAX).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MAX, MIN, MAX).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MAX, ylevel, MAX).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();
        builder.vertex(matrix, MIN, ylevel, MAX).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  0,  0,  1).endVertex();

        //East
        builder.vertex(matrix, MAX, MIN, MAX).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX, MIN, MIN).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX, ylevel, MIN).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();
        builder.vertex(matrix, MAX, ylevel, MAX).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal,  1,  0,  0).endVertex();

        //West
        builder.vertex(matrix, MIN, MIN, MIN).color(col[0], col[1], col[2], col[3]).uv(minU, minV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN, MIN, MAX).color(col[0], col[1], col[2], col[3]).uv(maxU, minV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN, ylevel, MAX).color(col[0], col[1], col[2], col[3]).uv(maxU, maxV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();
        builder.vertex(matrix, MIN, ylevel, MIN).color(col[0], col[1], col[2], col[3]).uv(minU, maxV).overlayCoords(overlay).uv2(240).normal(normal, -1,  0,  0).endVertex();

        stack.popPose();

    }



    public static int[] splitRGBA(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8)  & 0xFF;
        int b =  color        & 0xFF;
        int a = (color >> 24) & 0xFF;

        return new int[] { r, g, b, a };
    }
    



}
