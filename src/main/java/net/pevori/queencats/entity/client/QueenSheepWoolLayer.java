package net.pevori.queencats.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.HumanoidSheepEntity;
import net.pevori.queencats.entity.custom.QueenSheepEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class QueenSheepWoolLayer extends GeoRenderLayer<QueenSheepEntity> {
    private static final Identifier TEXTURE = new Identifier(QueenCats.MOD_ID, "textures/entity/queen_sheep/humanoid_sheep_wool.png");

    public QueenSheepWoolLayer(GeoRenderer<QueenSheepEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    public void render(MatrixStack poseStack, QueenSheepEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        float blue;
        float green;
        float red;

        final int COLOR_TRANSITION_TICKS = 25;
        final int TOTAL_COLORS = DyeColor.values().length;

        if (animatable.hasCustomName() && animatable.getName().getString().equals("jeb_")) {
            int colorCycleIndex = (animatable.age / COLOR_TRANSITION_TICKS) + animatable.getId();
            int currentColorIndex = colorCycleIndex % TOTAL_COLORS;
            int nextColorIndex = (colorCycleIndex + 1) % TOTAL_COLORS;

            float transitionProgress = ((animatable.age % COLOR_TRANSITION_TICKS) + partialTick) / COLOR_TRANSITION_TICKS;

            float[] currentColorRGB = HumanoidSheepEntity.getRgbColor(DyeColor.byId(currentColorIndex));
            float[] nextColorRGB = HumanoidSheepEntity.getRgbColor(DyeColor.byId(nextColorIndex));

            red = currentColorRGB[0] * (1.0f - transitionProgress) + nextColorRGB[0] * transitionProgress;
            green = currentColorRGB[1] * (1.0f - transitionProgress) + nextColorRGB[1] * transitionProgress;
            blue = currentColorRGB[2] * (1.0f - transitionProgress) + nextColorRGB[2] * transitionProgress;

        } else {
            float[] defaultColorRGB = HumanoidSheepEntity.getRgbColor(animatable.getColor());
            red = defaultColorRGB[0];
            green = defaultColorRGB[1];
            blue = defaultColorRGB[2];
        }

        RenderLayer armorRenderType = RenderLayer.getArmorCutoutNoCull(TEXTURE);
        this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType, bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }
}