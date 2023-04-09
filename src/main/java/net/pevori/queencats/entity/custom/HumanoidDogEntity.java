package net.pevori.queencats.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.variant.HumanoidDogVariant;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class HumanoidDogEntity extends HumanoidAnimalEntity implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    protected Item itemForTaming = ModItems.GOLDEN_BONE;
    protected Item itemForGrowth = ModItems.KEMOMIMI_POTION;
    public static final String koroSan = "korone";


    protected HumanoidDogEntity(EntityType<? extends TameableEntity> entityType, World world) {
    super(entityType, world);
    }

    @Override
    public PassiveEntity createChild(ServerWorld var1, PassiveEntity var2) {
        return null;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (this.hasStackEquipped(EquipmentSlot.CHEST)) {
            this.dropStack(getEquippedStack(EquipmentSlot.CHEST));
        }
        super.onDeath(source);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ModItems.KEMOMIMI_POTION;
    }

    public boolean isDoog(){
        String s = Formatting.strip(this.getName().getString());
        return (s != null && s.toLowerCase().contains(koroSan));
    }

    private PlayState predicate(AnimationState animationState) {
        if (this.isSitting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoiddog.sitting", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoiddog.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoiddog.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.humanoiddog.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",
                0, this::predicate));
        controllers.add(new AnimationController(this, "attackController",
                0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HUMANOID_DOG_AMBIENT;
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return ModSounds.HUMANOID_DOG_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.HUMANOID_DOG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HUMANOID_DOG_DEATH;
    }

    /*@Override
    protected SoundEvent getAttackSound(){
        return ModSounds.HUMANOID_DOG_ANGRY;
    }*/

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15f, 1.0f);
    }

    /* TAMEABLE ENTITY */
    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidDogEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public boolean isMeatItem(Item item) {
        return item.isFood() && item.getFoodComponent().isMeat();
    }

    public void startGrowth() {
        HumanoidDogVariant variant = this.getVariant();
        QueenDogEntity queenDogEntity = ModEntities.QUEEN_DOG.create(world);
        queenDogEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        queenDogEntity.setAiDisabled(this.isAiDisabled());

        queenDogEntity.setVariant(variant);

        if (this.hasCustomName()) {
            queenDogEntity.setCustomName(this.getCustomName());
            queenDogEntity.setCustomNameVisible(this.isCustomNameVisible());
        }
        queenDogEntity.setPersistent();
        queenDogEntity.setOwnerUuid(this.getOwnerUuid());
        queenDogEntity.setTamed(true);
        queenDogEntity.setSitting(this.isSitting());
        world.spawnEntity(queenDogEntity);
        this.discard();
    }

    @Override
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (target instanceof CreeperEntity || target instanceof GhastEntity) {
            return false;
        }
        if (target instanceof HumanoidBunnyEntity) {
            HumanoidBunnyEntity humanoidBunnyEntity = (HumanoidBunnyEntity) target;
            return !humanoidBunnyEntity.isTamed() || humanoidBunnyEntity.getOwner() != owner;
        }
        if (target instanceof HumanoidCatEntity) {
            HumanoidCatEntity humanoidCatEntity = (HumanoidCatEntity) target;
            return !humanoidCatEntity.isTamed() || humanoidCatEntity.getOwner() != owner;
        }
        if (target instanceof HumanoidDogEntity) {
            HumanoidDogEntity humanoidDogEntity = (HumanoidDogEntity) target;
            return !humanoidDogEntity.isTamed() || humanoidDogEntity.getOwner() != owner;
        }
        if (target instanceof PlayerEntity && owner instanceof PlayerEntity
                && !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) target)) {
            return false;
        }
        if (target instanceof HorseEntity && ((HorseEntity) target).isTame()) {
            return false;
        }
        return !(target instanceof TameableEntity) || !((TameableEntity) target).isTamed();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
    }

    @Override
    public AbstractTeam getScoreboardTeam() {
        return super.getScoreboardTeam();
    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
    }

    /* VARIANTS */
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(HumanoidDogEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public HumanoidDogVariant getVariant() {
        return HumanoidDogVariant.byId(this.getTypeVariant() & 255);
    }

    protected int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    protected void setVariant(HumanoidDogVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
