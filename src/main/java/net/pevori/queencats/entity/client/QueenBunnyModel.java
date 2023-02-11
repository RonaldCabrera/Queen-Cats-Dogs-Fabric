package net.pevori.queencats.entity.client;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.QueenBunnyEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class QueenBunnyModel extends AnimatedGeoModel<QueenBunnyEntity> {
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setLivingAnimations(QueenBunnyEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
