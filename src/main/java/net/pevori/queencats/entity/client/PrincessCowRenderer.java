package net.pevori.queencats.entity.client;

import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessCowEntity;
import net.pevori.queencats.entity.variant.HumanoidCowVariant;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrincessCowRenderer extends GeoEntityRenderer<PrincessCowEntity> {
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

    public PrincessCowRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PrincessCowModel());
    }

    @Override
    public RenderLayer getRenderType(PrincessCowEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
