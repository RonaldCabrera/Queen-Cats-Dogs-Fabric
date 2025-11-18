package net.pevori.queencats.entity.custom;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.variant.HumanoidDogVariant;
import net.pevori.queencats.item.ModItems;

import org.jetbrains.annotations.Nullable;

public class PrincessDogEntity extends HumanoidDogEntity{
    private final EntityType<QueenDogEntity> grownEntity = ModEntities.QUEEN_DOG;

    public PrincessDogEntity(EntityType<? extends HumanoidDogEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 1.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.25D, false));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.add(5, new TemptGoal(this, 1.0f, Ingredient.ofItems(ModItems.GOLDEN_BONE), false));
        this.goalSelector.add(5, new WanderAroundPointOfInterestGoal(this, 1.0f, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0, 1));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, AbstractSkeletonEntity.class, false));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        HumanoidDogVariant variant = Util.getRandom(HumanoidDogVariant.values(), this.random);
        setVariant(variant);

        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    public boolean isBaby() {
        return true;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();

        if (this.isTamed()){
            if (this.isOwner(player)) {
                if (item == itemForGrowth && isTamed() && this.isOwner(player) && !player.isSneaking()) {
                    itemStack.decrementUnlessCreative(1, player);
                    transformToQueen();
                    return ActionResult.success(this.getWorld().isClient());
                }
            }
        }

        return super.interactMob(player, hand);
    }

    public void transformToQueen() {
        var variant = this.getVariant();
        var queenEntity = grownEntity.create(this.getWorld());

        queenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        queenEntity.setAiDisabled(this.isAiDisabled());
        queenEntity.setInventory(this.inventory);

        if (this.hasCustomName()) {
            queenEntity.setCustomName(this.getCustomName());
            queenEntity.setCustomNameVisible(this.isCustomNameVisible());
        }

        queenEntity.setOwnerUuid(this.getOwnerUuid());
        queenEntity.setTamed(true, true);
        queenEntity.setSit(this.isSitting());
        queenEntity.setVariant(variant);
        queenEntity.setPersistent();

        this.getWorld().spawnEntity(queenEntity);
        this.discard();
    }
}