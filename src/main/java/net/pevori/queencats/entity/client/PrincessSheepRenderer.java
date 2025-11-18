package net.pevori.queencats.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessSheepEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrincessSheepRenderer extends GeoEntityRenderer<PrincessSheepEntity> {
    private static final Identifier TEXTURE = Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_sheep/humanoid_sheep.png");

    public PrincessSheepRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PrincessSheepModel());
        addRenderLayer(new PrincessSheepWoolLayer(this));
    }

    @Override
    public Identifier getTextureLocation(PrincessSheepEntity instance) {
        return TEXTURE;
    }

    @Override
    public RenderLayer getRenderType(PrincessSheepEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
