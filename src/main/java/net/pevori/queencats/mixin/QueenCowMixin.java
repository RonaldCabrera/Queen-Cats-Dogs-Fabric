package net.pevori.queencats.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.*;
import net.pevori.queencats.entity.variant.HumanoidCowVariant;
import net.pevori.queencats.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntity.class)
public abstract class QueenCowMixin extends AnimalEntity{

    protected QueenCowMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    //This Mixin injection is to make the cow turn into a humanoid cow when fed with a kemomimi potion.
    @Inject(method = "interactMob", at = @At("HEAD"))
    protected void injectInteractMethod(PlayerEntity player, Hand hand, CallbackInfoReturnable info) {
        Item usedItem = player.getStackInHand(hand).getItem();
        CowEntity cowEntity = ((CowEntity)(Object)this);

        if(usedItem == ModItems.KEMOMIMI_POTION){
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }

            HumanoidCowEntity humanoidCowEntity = cowEntity.isBaby()
                    ? ModEntities.PRINCESS_COW.create(cowEntity.world)
                    : ModEntities.QUEEN_COW.create(cowEntity.world);

            spawnHumanoidCow(humanoidCowEntity, cowEntity, player);
        }
    }

    public void spawnHumanoidCow(HumanoidCowEntity humanoidCowEntity, CowEntity cowEntity, PlayerEntity player){
        humanoidCowEntity.refreshPositionAndAngles(cowEntity.getX(), cowEntity.getY(), cowEntity.getZ(), cowEntity.getYaw(), cowEntity.getPitch());
        humanoidCowEntity.setAiDisabled(cowEntity.isAiDisabled());

        if (cowEntity.hasCustomName()) {
            humanoidCowEntity.setCustomName(cowEntity.getCustomName());
            humanoidCowEntity.setCustomNameVisible(cowEntity.isCustomNameVisible());
        }

        humanoidCowEntity.setPersistent();
        humanoidCowEntity.setOwnerUuid(player.getUuid());
        humanoidCowEntity.setTamed(true);
        humanoidCowEntity.setSit(true);

        HumanoidCowVariant variant = Util.getRandom(HumanoidCowVariant.values(), this.random);
        humanoidCowEntity.setVariant(variant);

        cowEntity.world.spawnEntity(humanoidCowEntity);
        cowEntity.discard();
    }
}