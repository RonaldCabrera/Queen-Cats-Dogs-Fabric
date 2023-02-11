package net.pevori.queencats.entity.client;

import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.PrincessDogEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class PrincessDogModel extends AnimatedGeoModel<PrincessDogEntity>{
    protected static final String koroSan = "Korone";

    @Override
    public Identifier getModelLocation(PrincessDogEntity object) {
        return new Identifier(QueenCats.MOD_ID, "geo/humanoid_dog_children.geo.json");
    }

    @Override
    public Identifier getTextureLocation(PrincessDogEntity object) {
        if(object.hasCustomName()){
            if(object.getCustomName().asString() == koroSan){
                return new Identifier(QueenCats.MOD_ID, "textures/entity/queen_dog/humanoid_dog_doog.png");
            }
        }

        return PrincessDogRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationFileLocation(PrincessDogEntity animatable) {
        return new Identifier(QueenCats.MOD_ID, "animations/humanoid_dog.animation.json");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setLivingAnimations(PrincessDogEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
