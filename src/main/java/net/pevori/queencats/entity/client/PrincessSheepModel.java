package net.pevori.queencats.entity.client;

import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessSheepEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PrincessSheepModel extends GeoModel<PrincessSheepEntity> {
    @Override
    public Identifier getModelResource(PrincessSheepEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_sheep_children.geo.json");
    }

    @Override
    public Identifier getTextureResource(PrincessSheepEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "textures/entity/queen_sheep/humanoid_sheep.png");
    }

    @Override
    public Identifier getAnimationResource(PrincessSheepEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_sheep.animation.json");
    }

    @Override
    public void setCustomAnimations(PrincessSheepEntity animatable, long instanceId, AnimationState<PrincessSheepEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");
        CoreGeoBone overgrown = getAnimationProcessor().getBone("overgrown");

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
