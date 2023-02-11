package net.pevori.queencats.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessBunnyEntity;
import net.pevori.queencats.entity.variant.HumanoidBunnyVariant;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Map;

public class PrincessBunnyRenderer extends GeoEntityRenderer<PrincessBunnyEntity> {
    public static final Map<HumanoidBunnyVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(HumanoidBunnyVariant.class), (map) -> {
                map.put(HumanoidBunnyVariant.COCOA,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_bunny/humanoid_bunny_cocoa.png"));
                map.put(HumanoidBunnyVariant.SNOW,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_bunny/humanoid_bunny_snow.png"));
                map.put(HumanoidBunnyVariant.SUNDAY,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_bunny/humanoid_bunny_sunday.png"));
                map.put(HumanoidBunnyVariant.STRAWBERRY,
                        new Identifier(QueenCats.MOD_ID, "textures/entity/queen_bunny/humanoid_bunny_strawberry.png"));
            });

    public PrincessBunnyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PrincessBunnyModel());
    }

    @Override
    public Identifier getTextureLocation(PrincessBunnyEntity instance) {
        if(instance.isAlmond()){
            return new Identifier(QueenCats.MOD_ID, "textures/entity/queen_bunny/humanoid_bunny_almond.png");
        }

        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }
}
