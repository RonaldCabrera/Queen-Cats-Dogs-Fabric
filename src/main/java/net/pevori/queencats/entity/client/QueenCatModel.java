package net.pevori.queencats.entity.client;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.QueenBunnyEntity;
import net.pevori.queencats.entity.custom.QueenCatEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class QueenCatModel extends GeoModel<QueenCatEntity> {
    @Override
    public Identifier getModelResource(QueenCatEntity entity) {
        if(entity.hasArmorInSlot()){
            return new Identifier(QueenCats.MOD_ID, "geo/humanoid_cat_armor.geo.json");
        }

        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_cat.geo.json");
    }

    @Override
    public Identifier getTextureResource(QueenCatEntity object) {
        return QueenCatRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(QueenCatEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_cat.animation.json");
    }

    @Override
    public void setCustomAnimations(QueenCatEntity animatable, long instanceId, AnimationState<QueenCatEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
