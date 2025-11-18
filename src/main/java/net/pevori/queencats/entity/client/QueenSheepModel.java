package net.pevori.queencats.entity.client;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.QueenSheepEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class QueenSheepModel  extends GeoModel<QueenSheepEntity> {
    @Override
    public Identifier getModelResource(QueenSheepEntity object) {
        if(object.hasStackEquipped(EquipmentSlot.CHEST)) {
            return Identifier.of(QueenCats.MOD_ID, "geo/humanoid_sheep_armor.geo.json");
        }

        return Identifier.of(QueenCats.MOD_ID, "geo/humanoid_sheep.geo.json");
    }

    @Override
    public Identifier getTextureResource(QueenSheepEntity queenEntity) {
        return Identifier.of(QueenCats.MOD_ID, "textures/entity/queen_sheep/humanoid_sheep.png");
    }

    @Override
    public Identifier getAnimationResource(QueenSheepEntity animatable) {
        return Identifier.of(QueenCats.MOD_ID, "animations/humanoid_sheep.animation.json");
    }

    @Override
    public void setCustomAnimations(QueenSheepEntity animatable, long instanceId, AnimationState<QueenSheepEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        var head = getAnimationProcessor().getBone("head");
        var overgrown = getAnimationProcessor().getBone("overgrown");

        if (overgrown != null) {
            overgrown.setHidden(animatable.isSheared());
        }

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
