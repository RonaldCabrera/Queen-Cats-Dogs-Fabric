package net.pevori.queencats.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.HumanoidCowEntity;
import net.pevori.queencats.entity.custom.PrincessCowEntity;
import net.pevori.queencats.entity.custom.QueenBunnyEntity;
import net.pevori.queencats.entity.custom.QueenCowEntity;
import net.pevori.queencats.entity.variant.HumanoidCowVariant;
import net.pevori.queencats.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CowEntity.class)
public abstract class QueenCowMixin extends AnimalEntity{

    protected QueenCowMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    //This Mixin injection is to make the cow turn into a humanoid cow when fed with golden wheat.
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        CowEntity cowEntity = ((CowEntity)(Object)this);

        /*
         * This is the logic to generate a new Queen Cow, pretty much the same as
         * a pig getting hit by lightning.
         */
        if(itemStack.getItem() == ModItems.KEMOMIMI_POTION){
            if (!player.getAbilities().creativeMode) {
                player.getStackInHand(hand).decrement(1);
            }

            if(cowEntity.isBaby()){
                PrincessCowEntity princessCowEntity = ModEntities.PRINCESS_COW.create(cowEntity.world);
                spawnHumanoidCow(princessCowEntity, cowEntity, player);
            }
            else {
                QueenCowEntity queenCowEntity = ModEntities.QUEEN_COW.create(cowEntity.world);
                spawnHumanoidCow(queenCowEntity, cowEntity, player);
            }
        }

        return super.interactMob(player, hand);
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
