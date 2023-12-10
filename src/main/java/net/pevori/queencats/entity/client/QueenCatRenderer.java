package net.pevori.queencats.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.QueenCatEntity;
import net.pevori.queencats.entity.variant.HumanoidCatVariant;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

public class QueenCatRenderer extends GeoEntityRenderer<QueenCatEntity> {
    public static final Map<HumanoidCatVariant, Identifier> LOCATION_BY_VARIANT =
    Util.make(Maps.newEnumMap(HumanoidCatVariant.class), (map) -> {
        map.put(HumanoidCatVariant.WHITE,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_white.png"));
        map.put(HumanoidCatVariant.BLACK,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_black.png"));
        map.put(HumanoidCatVariant.CALICO,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_calico.png"));
        map.put(HumanoidCatVariant.CALLAS,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_callas.png"));
    });

    public QueenCatRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new QueenCatModel());
    }

    @Override
    public Identifier getTextureLocation(QueenCatEntity instance) {
        if(instance.isMogu()){
            return new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_mogu.png");
        }

        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }

    @Override
    public RenderLayer getRenderType(QueenCatEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
