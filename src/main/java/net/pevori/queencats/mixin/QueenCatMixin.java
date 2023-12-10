package net.pevori.queencats.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.HumanoidCatEntity;
import net.pevori.queencats.entity.custom.PrincessCatEntity;
import net.pevori.queencats.entity.custom.QueenCatEntity;
import net.pevori.queencats.entity.variant.HumanoidCatVariant;
import net.pevori.queencats.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatEntity.class)
public abstract class QueenCatMixin extends AnimalEntity {
    protected QueenCatMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    /*
     * This Mixin injection is to make the cat turn into a queen cat
     * when fed with golden fish. 
     */
    @Inject(method = "interactMob", at = @At("HEAD"))
    protected void injectInteractMethod(PlayerEntity player, Hand hand, CallbackInfoReturnable info) {
        CatEntity thisCat = ((CatEntity)(Object)this);
        Item usedItem = player.getStackInHand(hand).getItem();

        //This is the logic to generate a new Humanoid Cat, pretty much the same as a pig getting hit by lightning.
        if(thisCat.isTamed() && thisCat.isOwner(player) && usedItem == ModItems.KEMOMIMI_POTION){
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }

            if(thisCat.isBaby()){
                PrincessCatEntity princessCatEntity = ModEntities.PRINCESS_CAT.create(thisCat.getWorld());
                spawnHumanoidCat(princessCatEntity, thisCat, player);
            }
            else {
                QueenCatEntity queenCatEntity = ModEntities.QUEEN_CAT.create(thisCat.getWorld());
                spawnHumanoidCat(queenCatEntity, thisCat, player);
            }
        }
    }

    public void spawnHumanoidCat(HumanoidCatEntity humanoidCatEntity, CatEntity catEntity, PlayerEntity player){
        humanoidCatEntity.refreshPositionAndAngles(catEntity.getX(), catEntity.getY(), catEntity.getZ(), catEntity.getYaw(), catEntity.getPitch());
        humanoidCatEntity.setAiDisabled(catEntity.isAiDisabled());

        if (catEntity.hasCustomName()) {
            humanoidCatEntity.setCustomName(catEntity.getCustomName());
            humanoidCatEntity.setCustomNameVisible(catEntity.isCustomNameVisible());
        }

        humanoidCatEntity.setPersistent();
        humanoidCatEntity.setOwnerUuid(player.getUuid());
        humanoidCatEntity.setTamed(true);
        humanoidCatEntity.setSit(true);

        HumanoidCatVariant variant = Util.getRandom(HumanoidCatVariant.values(), this.random);
        humanoidCatEntity.setVariant(variant);

        catEntity.getWorld().spawnEntity(humanoidCatEntity);
        catEntity.discard();
    }
}
