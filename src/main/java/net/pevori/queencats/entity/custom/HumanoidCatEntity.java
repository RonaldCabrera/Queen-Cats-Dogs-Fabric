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
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.pevori.queencats.config.QueenCatsConfig;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.variant.HumanoidCatVariant;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static net.pevori.queencats.sound.ModSounds.soundEventByConfig;

public class HumanoidCatEntity extends HumanoidAnimalEntity implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    protected Item itemForTaming = ModItems.GOLDEN_FISH;
    protected Item itemForGrowth = ModItems.KEMOMIMI_POTION;
    protected Ingredient itemForHealing = Ingredient.ofItems(Items.COD, Items.SALMON, ModItems.GOLDEN_FISH);
    public static final String okayuSan = "okayu";

    protected HumanoidCatEntity(EntityType<? extends HumanoidAnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public PassiveEntity createChild(ServerWorld var1, PassiveEntity var2) {
        return null;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    public boolean isMogu(){
        String s = Formatting.strip(this.getName().getString());
        return (s != null && s.toLowerCase().contains(okayuSan));
    }

    public void startGrowth() {
        HumanoidCatVariant variant = this.getVariant();
        QueenCatEntity queenCatEntity = ModEntities.QUEEN_CAT.create(world);
        queenCatEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        queenCatEntity.setAiDisabled(this.isAiDisabled());
        queenCatEntity.setInventory(this.inventory);

        queenCatEntity.setVariant(variant);

        if (this.hasCustomName()) {
            queenCatEntity.setCustomName(this.getCustomName());
            queenCatEntity.setCustomNameVisible(this.isCustomNameVisible());
        }

        queenCatEntity.setPersistent();
        queenCatEntity.setOwnerUuid(this.getOwnerUuid());
        queenCatEntity.setTamed(true);
        queenCatEntity.setSitting(this.isSitting());
        world.spawnEntity(queenCatEntity);
        this.discard();
    }

    private PlayState predicate(AnimationState animationState) {
        if (this.isSitting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcat.sitting", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcat.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcat.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcat.attack", Animation.LoopType.PLAY_ONCE));
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
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_AMBIENT);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_EAT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_HURT);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_DEATH);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15f, 1.0f);
    }

    /* TAMEABLE ENTITY */
    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidCatEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ModItems.KEMOMIMI_POTION;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (this.hasStackEquipped(EquipmentSlot.CHEST)) {
            this.dropStack(getEquippedStack(EquipmentSlot.CHEST));
        }

        super.onDeath(source);
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

    /*VARIANTS*/
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(HumanoidCatEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public HumanoidCatVariant getVariant() {
        return HumanoidCatVariant.byId(this.getTypeVariant() & 255);
    }

    protected int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(HumanoidCatVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
