package net.pevori.queencats.entity.client;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessCatEntity;
import net.pevori.queencats.entity.variant.HumanoidCatVariant;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrincessCatRenderer extends GeoEntityRenderer<PrincessCatEntity> {
    public static final Map<HumanoidCatVariant, Identifier> LOCATION_BY_VARIANT =
    Util.make(Maps.newEnumMap(HumanoidCatVariant.class), (map) -> {
        map.put(HumanoidCatVariant.WHITE,
                Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_white.png"));
        map.put(HumanoidCatVariant.BLACK,
                Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_black.png"));
        map.put(HumanoidCatVariant.CALICO,
                Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_calico.png"));
        map.put(HumanoidCatVariant.CALLAS,
                Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_callas.png"));
    });

    public PrincessCatRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PrincessCatModel());
    }

    @Override
    public Identifier getTextureLocation(PrincessCatEntity instance) {
        if(instance.hasHoloNameEasterEgg()) {
            return Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_cat/humanoid_cat_mogu.png");
        }
        
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }

    @Override
    public RenderLayer getRenderType(PrincessCatEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
