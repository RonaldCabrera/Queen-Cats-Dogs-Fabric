package net.pevori.queencats.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.QueenCowEntity;
import net.pevori.queencats.entity.variant.HumanoidCowVariant;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class QueenCowRenderer extends GeoEntityRenderer<QueenCowEntity> {
    public static final Map<HumanoidCowVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(HumanoidCowVariant.class), (map) -> {
                map.put(HumanoidCowVariant.COFFEE,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cow/coffee.png"));
                map.put(HumanoidCowVariant.MILKSHAKE,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cow/milkshake.png"));
                map.put(HumanoidCowVariant.MOOSHROOM,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cow/mooshroom.png"));
                map.put(HumanoidCowVariant.MOOBLOOM,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cow/moobloom.png"));
                map.put(HumanoidCowVariant.WOOLY,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_cow/wooly.png"));
            });

    public QueenCowRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new QueenCowModel());
    }

    @Override
    public RenderLayer getRenderType(QueenCowEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
