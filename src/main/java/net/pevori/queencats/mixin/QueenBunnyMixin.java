package net.pevori.queencats.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.QueenBunnyEntity;
import net.pevori.queencats.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RabbitEntity.class)
public abstract class QueenBunnyMixin extends AnimalEntity{

    protected QueenBunnyMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    /*
     * This Mixin injection is to make the rabbit turn into a queen bunny
     * when fed with golden wheat.
     */
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        RabbitEntity thisRabbit = ((RabbitEntity)(Object)this);

        /*
         * This is the logic to generate a new Queen Bunny, pretty much the same as
         * a pig getting hit by lightning.
         */
        if(itemStack.getItem() == ModItems.KEMOMIMI_POTION){
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }

            QueenBunnyEntity queenBunnyEntity = ModEntities.QUEEN_BUNNY.create(thisRabbit.world);
            queenBunnyEntity.refreshPositionAndAngles(thisRabbit.getX(), thisRabbit.getY(), thisRabbit.getZ(), thisRabbit.getYaw(), thisRabbit.getPitch());
            queenBunnyEntity.setAiDisabled(thisRabbit.isAiDisabled());

            if (thisRabbit.hasCustomName()) {
                queenBunnyEntity.setCustomName(thisRabbit.getCustomName());
                queenBunnyEntity.setCustomNameVisible(thisRabbit.isCustomNameVisible());
            }

            queenBunnyEntity.setPersistent();
            queenBunnyEntity.setOwnerUuid(player.getUuid());
            queenBunnyEntity.setTamed(true);
            queenBunnyEntity.setSit(true);
            thisRabbit.world.spawnEntity(queenBunnyEntity);
            thisRabbit.discard();
        }

        return super.interactMob(player, hand);
    }


}
