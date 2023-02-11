package net.pevori.queencats.entity.client;

import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessBunnyEntity;
import net.pevori.queencats.entity.custom.PrincessDogEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PrincessDogModel extends GeoModel<PrincessDogEntity> {
    protected static final String koroSan = "Korone";

    @Override
    public Identifier getModelResource(PrincessDogEntity object) {
        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_dog_children.geo.json");
    }

    @Override
    public Identifier getTextureResource(PrincessDogEntity object) {
        if(object.hasCustomName()){
            if(object.getCustomName().toString() == koroSan){
                return new Identifier(QueenCats.MOD_ID, "textures/entity/queen_dog/humanoid_dog_doog.png");
            }
        }

        return PrincessDogRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(PrincessDogEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_dog.animation.json");
    }

    @Override
    public void setCustomAnimations(PrincessDogEntity animatable, long instanceId, AnimationState<PrincessDogEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null) {
            //head.setRotX((entityData.headPitch() - 5) * 0.0174533f);
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
