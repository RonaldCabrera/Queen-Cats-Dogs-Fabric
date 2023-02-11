package net.pevori.queencats.entity.client;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessBunnyEntity;
import net.pevori.queencats.entity.custom.QueenDogEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class QueenDogModel extends GeoModel<QueenDogEntity> {
    @Override
    public Identifier getModelResource(QueenDogEntity object) {
        if(object.hasStackEquipped(EquipmentSlot.CHEST)){
            return new Identifier(QueenCats.MOD_ID, "geo/humanoid_dog_armor.geo.json");
        }

        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_dog.geo.json");
    }

    @Override
    public Identifier getTextureResource(QueenDogEntity queenDogEntity) {
        return QueenDogRenderer.LOCATION_BY_VARIANT.get(queenDogEntity.getVariant());
    }

    @Override
    public Identifier getAnimationResource(QueenDogEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_dog.animation.json");
    }

    @Override
    public void setCustomAnimations(QueenDogEntity animatable, long instanceId, AnimationState<QueenDogEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
