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
import net.pevori.queencats.entity.custom.PrincessSheepEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PrincessSheepWoolLayer extends GeoRenderLayer<PrincessSheepEntity> {
    private static final Identifier TEXTURE = new Identifier(QueenCats.MOD_ID, "textures/entity/queen_sheep/humanoid_sheep_wool.png");

    public PrincessSheepWoolLayer(GeoRenderer<PrincessSheepEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    public void render(MatrixStack poseStack, PrincessSheepEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        float blue;
        float green;
        float red;

        if (animatable.hasCustomName() && animatable.getName().getString().equals("jeb_")) {
            int m = 25;
            int n = animatable.age / 25 + animatable.getId();
            int o = DyeColor.values().length;
            int p = n % o;
            int q = (n + 1) % o;
            float r = ((float)(animatable.age % 25) + partialTick) / 25.0f;
            float[] fs = HumanoidSheepEntity.getRgbColor(DyeColor.byId(p));
            float[] gs = HumanoidSheepEntity.getRgbColor(DyeColor.byId(q));
            red = fs[0] * (1.0f - r) + gs[0] * r;
            green = fs[1] * (1.0f - r) + gs[1] * r;
            blue = fs[2] * (1.0f - r) + gs[2] * r;
        } else {
            float[] hs = HumanoidSheepEntity.getRgbColor(animatable.getColor());
            red = hs[0];
            green = hs[1];
            blue = hs[2];
        }

        RenderLayer armorRenderType = RenderLayer.getArmorCutoutNoCull(TEXTURE);
        this.getRenderer().reRender(this.getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType, bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
    }
}