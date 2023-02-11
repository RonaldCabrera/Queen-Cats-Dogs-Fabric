package net.pevori.queencats.entity.client;

import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessBunnyEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PrincessBunnyModel extends GeoModel<PrincessBunnyEntity> {
    @Override
    public Identifier getModelResource(PrincessBunnyEntity object) {
        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_bunny_children.geo.json");
    }

    @Override
    public Identifier getTextureResource(PrincessBunnyEntity object) {
        return PrincessBunnyRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(PrincessBunnyEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_bunny.animation.json");
    }

    @Override
    public void setCustomAnimations(PrincessBunnyEntity animatable, long instanceId, AnimationState<PrincessBunnyEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
