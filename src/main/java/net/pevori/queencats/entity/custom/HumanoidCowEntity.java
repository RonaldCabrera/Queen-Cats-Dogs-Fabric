package net.pevori.queencats.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pevori.queencats.config.QueenCatsConfig;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.variant.HumanoidCowVariant;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static net.pevori.queencats.sound.ModSounds.soundEventByConfig;

public class HumanoidCowEntity extends HumanoidAnimalEntity implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    protected Item itemForTaming = ModItems.GOLDEN_WHEAT;
    protected Ingredient itemForHealing = Ingredient.ofItems(Items.WHEAT, ModItems.GOLDEN_WHEAT);
    protected Item itemForGrowth = ModItems.KEMOMIMI_POTION;

    protected HumanoidCowEntity(EntityType<? extends TameableEntity> entityType, World world) {
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

    private PlayState predicate(AnimationState animationState) {
        if (this.isSitting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcow.sitting", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcow.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcow.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcow.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",
                0, this::predicate));
        controllers.add(new AnimationController<>(this, "attackController",
                0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCowSounds, ModSounds.HUMANOID_COW_AMBIENT);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCowSounds, ModSounds.HUMANOID_COW_EAT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCowSounds, ModSounds.HUMANOID_COW_HURT);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCowSounds, ModSounds.HUMANOID_COW_DEATH);
    }

    protected SoundEvent getMilkingSound(){
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCowSounds, ModSounds.HUMANOID_COW_MILK);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15f, 1.0f);
    }

    /* TAMEABLE ENTITY */
    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidCowEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void startGrowth() {
        HumanoidCowVariant variant = this.getVariant();
        QueenCowEntity queenCowEntity = ModEntities.QUEEN_COW.create(world);
        queenCowEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        queenCowEntity.setAiDisabled(this.isAiDisabled());
        queenCowEntity.setInventory(this.inventory);

        queenCowEntity.setVariant(variant);

        if (this.hasCustomName()) {
            queenCowEntity.setCustomName(this.getCustomName());
            queenCowEntity.setCustomNameVisible(this.isCustomNameVisible());
        }

        queenCowEntity.setPersistent();
        queenCowEntity.setOwnerUuid(this.getOwnerUuid());
        queenCowEntity.setTamed(true);
        queenCowEntity.setSitting(this.isSitting());
        world.spawnEntity(queenCowEntity);
        this.discard();
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
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(HumanoidCowEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public boolean isMilkableVariant(){
        HumanoidCowVariant variant = this.getVariant();
        return variant != HumanoidCowVariant.MOOSHROOM && variant != HumanoidCowVariant.MOOBLOOM;
    }

    public boolean isStewableVariant(){
        HumanoidCowVariant variant = this.getVariant();
        return variant == HumanoidCowVariant.MOOSHROOM || variant == HumanoidCowVariant.MOOBLOOM;
    }

    public HumanoidCowVariant getVariant() {
        return HumanoidCowVariant.byId(this.getTypeVariant() & 255);
    }

    protected int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(HumanoidCowVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
