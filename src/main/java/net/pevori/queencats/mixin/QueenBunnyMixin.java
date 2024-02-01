package net.pevori.queencats.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.*;
import net.pevori.queencats.entity.variant.HumanoidBunnyVariant;
import net.pevori.queencats.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RabbitEntity.class)
public abstract class QueenBunnyMixin extends AnimalEntity{

    protected QueenBunnyMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        RabbitEntity rabbitEntity = ((RabbitEntity)(Object)this);

        if(itemStack.getItem() == ModItems.KEMOMIMI_POTION){
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }

            HumanoidBunnyEntity humanoidBunnyEntity = rabbitEntity.isBaby()
                    ? ModEntities.PRINCESS_BUNNY.create(rabbitEntity.getWorld())
                    : ModEntities.QUEEN_BUNNY.create(rabbitEntity.getWorld());

            spawnHumanoidBunny(humanoidBunnyEntity, rabbitEntity, player);
        }

        return super.interactMob(player, hand);
    }

    public void spawnHumanoidBunny(HumanoidBunnyEntity humanoidBunnyEntity, RabbitEntity rabbitEntity, PlayerEntity player){
        humanoidBunnyEntity.refreshPositionAndAngles(rabbitEntity.getX(), rabbitEntity.getY(), rabbitEntity.getZ(), rabbitEntity.getYaw(), rabbitEntity.getPitch());
        humanoidBunnyEntity.setAiDisabled(rabbitEntity.isAiDisabled());

        if (rabbitEntity.hasCustomName()) {
            humanoidBunnyEntity.setCustomName(rabbitEntity.getCustomName());
            humanoidBunnyEntity.setCustomNameVisible(rabbitEntity.isCustomNameVisible());
        }

        humanoidBunnyEntity.setPersistent();
        humanoidBunnyEntity.setOwnerUuid(player.getUuid());
        humanoidBunnyEntity.setTamed(true);
        humanoidBunnyEntity.setSit(true);

        HumanoidBunnyVariant variant = Util.getRandom(HumanoidBunnyVariant.values(), this.random);
        humanoidBunnyEntity.setVariant(variant);

        rabbitEntity.getWorld().spawnEntity(humanoidBunnyEntity);
        rabbitEntity.discard();
    }
}