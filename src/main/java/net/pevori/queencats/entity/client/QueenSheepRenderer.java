package net.pevori.queencats.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.QueenSheepEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class QueenSheepRenderer extends GeoEntityRenderer<QueenSheepEntity> {
    private static final Identifier TEXTURE = new Identifier(QueenCats.MOD_ID, "textures/entity/queen_sheep/humanoid_sheep.png");

    public QueenSheepRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new QueenSheepModel());
        addRenderLayer(new QueenSheepWoolLayer(this));
    }

    @Override
    public Identifier getTextureLocation(QueenSheepEntity instance) {
        return TEXTURE;
    }

    @Override
    public RenderLayer getRenderType(QueenSheepEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
