package net.pevori.queencats.entity.client;

import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessCowEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PrincessCowModel extends GeoModel<PrincessCowEntity> {
    @Override
    public Identifier getModelResource(PrincessCowEntity object) {
        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_cow_children.geo.json");
    }

    @Override
    public Identifier getTextureResource(PrincessCowEntity object) {
        return PrincessCowRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(PrincessCowEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_cow.animation.json");
    }

    @Override
    public void setCustomAnimations(PrincessCowEntity animatable, long instanceId, AnimationState<PrincessCowEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            head.setPivotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setPivotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
