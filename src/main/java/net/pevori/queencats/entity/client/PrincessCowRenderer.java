package net.pevori.queencats.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessCowEntity;
import net.pevori.queencats.entity.variant.HumanoidCowVariant;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Map;

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
    public Identifier getTextureResource(PrincessCowEntity instance) {
        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }
}
