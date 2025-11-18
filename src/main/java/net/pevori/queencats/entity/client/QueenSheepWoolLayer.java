package net.pevori.queencats.entity.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.HumanoidSheepEntity;
import net.pevori.queencats.entity.custom.QueenSheepEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class QueenSheepWoolLayer extends GeoRenderLayer<QueenSheepEntity> {
    private static final Identifier TEXTURE = Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_sheep/humanoid_sheep_wool.png");

    public QueenSheepWoolLayer(GeoRenderer<QueenSheepEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    public void render(MatrixStack poseStack, QueenSheepEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        int colour;

        if (animatable.hasCustomName() && animatable.getName().getString().equals("jeb_")) {
            int totalColors = DyeColor.values().length;
            int colorCycleIndex = animatable.age / 25 + animatable.getId();
            int currentColorIndex = colorCycleIndex % totalColors;
            int nextColorIndex = (colorCycleIndex + 1) % totalColors;
            float blendFactor = (animatable.age % 25 + partialTick) / 25.0F;
            int currentColorRgb = SheepEntity.getRgbColor(DyeColor.byId(currentColorIndex));
            int nextColorRgb = SheepEntity.getRgbColor(DyeColor.byId(nextColorIndex));

            colour = ColorHelper.Argb.lerp(blendFactor, currentColorRgb, nextColorRgb);
        } else {
            colour = SheepEntity.getRgbColor(animatable.getColor());
        }

        RenderLayer armorRenderType = RenderLayer.getEntityCutoutNoCull(TEXTURE);
        this.getRenderer().reRender(
                this.getDefaultBakedModel(animatable),
                poseStack,
                bufferSource,
                animatable,
                armorRenderType,
                bufferSource.getBuffer(armorRenderType),
                partialTick,
                packedLight,
                OverlayTexture.DEFAULT_UV,
                colour);
    }
}