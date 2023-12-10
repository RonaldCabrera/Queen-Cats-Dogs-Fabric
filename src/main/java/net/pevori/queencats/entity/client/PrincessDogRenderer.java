package net.pevori.queencats.entity.client;

import com.google.common.collect.Maps;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessDogEntity;
import net.pevori.queencats.entity.variant.HumanoidDogVariant;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

public class PrincessDogRenderer extends GeoEntityRenderer<PrincessDogEntity> {
    public static final Map<HumanoidDogVariant, Identifier> LOCATION_BY_VARIANT =
    Util.make(Maps.newEnumMap(HumanoidDogVariant.class), (map) -> {
        map.put(HumanoidDogVariant.SHIRO,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_dog/humanoid_dog_shiro.png"));
        map.put(HumanoidDogVariant.HUSKY,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_dog/humanoid_dog_husky.png"));
        map.put(HumanoidDogVariant.CREAM,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_dog/humanoid_dog_cream.png"));
        map.put(HumanoidDogVariant.GRAY,
                new Identifier(QueenCats.MOD_ID, "textures/entity/queen_dog/humanoid_dog_gray.png"));
    });

    public PrincessDogRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PrincessDogModel());
    }

    @Override
    public Identifier getTextureLocation(PrincessDogEntity instance) {
        if(instance.isDoog()){
            return new Identifier(QueenCats.MOD_ID, "textures/entity/queen_dog/humanoid_dog_doog.png");
        }

        return LOCATION_BY_VARIANT.get(instance.getVariant());
    }

    @Override
    public RenderLayer getRenderType(PrincessDogEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
