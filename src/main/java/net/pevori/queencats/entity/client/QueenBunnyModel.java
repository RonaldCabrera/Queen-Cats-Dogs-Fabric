package net.pevori.queencats.entity.client;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.QueenBunnyEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class QueenBunnyModel extends GeoModel<QueenBunnyEntity> {
    @Override
    public Identifier getModelResource(QueenBunnyEntity object) {
        if(object.hasStackEquipped(EquipmentSlot.CHEST)){
            return new Identifier(QueenCats.MOD_ID, "geo/humanoid_bunny_armor.geo.json");
        }

        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_bunny.geo.json");
    }

    @Override
    public Identifier getTextureResource(QueenBunnyEntity object) {
        return QueenBunnyRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(QueenBunnyEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_bunny.animation.json");
    }

    @Override
    public void setCustomAnimations(QueenBunnyEntity animatable, long instanceId, AnimationState<QueenBunnyEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
