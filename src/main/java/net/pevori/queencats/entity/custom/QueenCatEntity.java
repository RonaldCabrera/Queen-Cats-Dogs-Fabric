package net.pevori.queencats.entity.custom;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.variant.HumanoidCatVariant;
import net.pevori.queencats.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class QueenCatEntity extends HumanoidCatEntity{
    public QueenCatEntity(EntityType<? extends HumanoidCatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        PrincessCatEntity baby = ModEntities.PRINCESS_CAT.create(world);
        HumanoidCatVariant variant = Util.getRandom(HumanoidCatVariant.values(), this.random);
        baby.setVariant(variant);

        if (this.isTamed()) {
            baby.setOwnerUuid(this.getOwnerUuid());
            baby.setTamed(true);
        }

        return baby;
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.25D, false));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 4.0f, 10.0f, false));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new TemptGoal(this, 1.0f, Ingredient.ofItems(ModItems.GOLDEN_FISH), false));
        this.goalSelector.add(5, new WanderAroundPointOfInterestGoal(this, 1.0f, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0, 1));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
    }

    /* TAMEABLE ENTITY */
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();

        if (isBreedingItem(itemStack) && !player.isSneaking()) {
            return super.interactMob(player, hand);
        }

        if (item instanceof DyeItem && this.isOwner(player) && !player.isSneaking()) {
            DyeColor dyeColor = ((DyeItem) item).getColor();
            if (dyeColor == DyeColor.BLACK) {
                this.setVariant(HumanoidCatVariant.BLACK);
            } else if (dyeColor == DyeColor.WHITE) {
                this.setVariant(HumanoidCatVariant.WHITE);
            } else if (dyeColor == DyeColor.ORANGE) {
                this.setVariant(HumanoidCatVariant.CALICO);
            } else if (dyeColor == DyeColor.GRAY) {
                this.setVariant(HumanoidCatVariant.CALLAS);
            }
            
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            this.setPersistent();
            return ActionResult.CONSUME;
        }

//        if (this.hasStackEquipped(EquipmentSlot.CHEST) && isTamed() && this.isOwner(player) && !this.world.isClient() && hand == Hand.MAIN_HAND
//                && player.isSneaking()) {
//            if (!player.getAbilities().creativeMode) {
//                player.giveItemStack(this.getEquippedStack(EquipmentSlot.CHEST));
//            }
//            this.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
//
//            return ActionResult.CONSUME;
//        } else if (equippableArmor.test(itemStack) && isTamed() && this.isOwner(player) && !this.hasStackEquipped(EquipmentSlot.CHEST)) {
//            this.equipStack(EquipmentSlot.CHEST, itemStack.copy());
//            if (!player.getAbilities().creativeMode) {
//                itemStack.decrement(1);
//            }
//
//            return ActionResult.SUCCESS;
//        }

        if ((itemForHealing.test(itemStack)) && isTamed() && this.getHealth() < getMaxHealth()) {
            if (this.world.isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (!this.world.isClient()) {
                    this.eat(player, hand, itemStack);
                    this.heal(10.0f);

                    if (this.getHealth() > getMaxHealth()) {
                        this.setHealth(getMaxHealth());
                    }

                    this.playSound(this.getEatSound(itemStack), 1.0f, 1.0f);
                }

                return ActionResult.SUCCESS;
            }
        }

        else if (item == itemForTaming && !isTamed()) {
            if (this.world.isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (!this.world.isClient()) {
                    this.playSound(this.getEatSound(itemStack), 1.0f, 1.0f);
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.world.sendEntityStatus(this, (byte) 7);
                    setSit(true);
                    this.setHealth(getMaxHealth());
                }

                return ActionResult.SUCCESS;
            }
        }

        if (isTamed() && this.isOwner(player) && !player.isSneaking() && !this.world.isClient() && hand == Hand.MAIN_HAND) {
            setSit(!isSitting());
            return ActionResult.SUCCESS;
        }

        if (itemStack.getItem() == itemForTaming) {
            return ActionResult.PASS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        if (tamed) {
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(60.0D);
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0D);
            getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3f);
        } else {
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0D);
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(3.0D);
            getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3f);
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
            @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        HumanoidCatVariant variant = Util.getRandom(HumanoidCatVariant.values(), this.random);
        setVariant(variant);

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
}