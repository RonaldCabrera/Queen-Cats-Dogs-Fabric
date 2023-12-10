package net.pevori.queencats.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.pevori.queencats.entity.custom.*;
import net.pevori.queencats.entity.variant.HumanoidCatVariant;
import net.pevori.queencats.entity.variant.HumanoidDogVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.item.ModItems;

@Mixin(WolfEntity.class)
public abstract class QueenDogMixin extends AnimalEntity {
    protected QueenDogMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    // This Mixin injection is to make the Wolf turn into a Humanoid Dog when fed with golden bone.
    @Inject(method = "interactMob", at = @At("HEAD"))
    protected void injectInteractMethod(PlayerEntity player, Hand hand, CallbackInfoReturnable info) {
        WolfEntity thisWolf = ((WolfEntity)(Object)this);
        Item usedItem = player.getStackInHand(hand).getItem();

        // This is the logic to generate a new Humanoid Dog, pretty much the same as a pig getting hit by lightning.
        if(thisWolf.isTamed() && thisWolf.isOwner(player) && usedItem == ModItems.KEMOMIMI_POTION){
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }

            if(thisWolf.isBaby()){
                PrincessDogEntity princessCatEntity = ModEntities.PRINCESS_DOG.create(thisWolf.getWorld());
                spawnHumanoidDog(princessCatEntity, thisWolf, player);
            }
            else {
                QueenDogEntity queenCatEntity = ModEntities.QUEEN_DOG.create(thisWolf.getWorld());
                spawnHumanoidDog(queenCatEntity, thisWolf, player);
            }
        }
    }

    public void spawnHumanoidDog(HumanoidDogEntity humanoidDogEntity, WolfEntity wolfEntity, PlayerEntity player){
        humanoidDogEntity.refreshPositionAndAngles(wolfEntity.getX(), wolfEntity.getY(), wolfEntity.getZ(), wolfEntity.getYaw(), wolfEntity.getPitch());
        humanoidDogEntity.setAiDisabled(wolfEntity.isAiDisabled());

        if (wolfEntity.hasCustomName()) {
            humanoidDogEntity.setCustomName(wolfEntity.getCustomName());
            humanoidDogEntity.setCustomNameVisible(wolfEntity.isCustomNameVisible());
        }

        humanoidDogEntity.setPersistent();
        humanoidDogEntity.setOwnerUuid(player.getUuid());
        humanoidDogEntity.setTamed(true);
        humanoidDogEntity.setSit(true);

        HumanoidDogVariant variant = Util.getRandom(HumanoidDogVariant.values(), this.random);
        humanoidDogEntity.setVariant(variant);

        wolfEntity.getWorld().spawnEntity(humanoidDogEntity);
        wolfEntity.discard();
    }
}
