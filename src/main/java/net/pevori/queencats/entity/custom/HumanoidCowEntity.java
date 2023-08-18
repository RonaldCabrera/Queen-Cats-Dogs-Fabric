package net.pevori.queencats.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pevori.queencats.config.QueenCatsConfig;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.variant.HumanoidCowVariant;
import net.pevori.queencats.entity.variant.HumanoidDogVariant;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class HumanoidCowEntity extends HumanoidAnimalEntity implements IAnimatable{
    protected AnimationFactory factory = new AnimationFactory(this);
    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidCowEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(HumanoidCowEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    protected Item itemForTaming = ModItems.GOLDEN_WHEAT;
    protected Item itemForGrowth = ModItems.KEMOMIMI_POTION;

    protected HumanoidCowEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public PassiveEntity createChild(ServerWorld var1, PassiveEntity var2) {
        return null;
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

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.isSitting()) {
            event.getController()
                    .setAnimation(new AnimationBuilder().addAnimation("animation.humanoidcow.sitting", true));
            return PlayState.CONTINUE;
        }

        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.humanoidcow.walk", true));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.humanoidcow.idle", true));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationEvent event) {
        if(this.handSwinging && event.getController().getAnimationState().equals(AnimationState.Stopped)) {
            event.getController().markNeedsReload();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.humanoidcow.attack", false));
            this.handSwinging = false;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller",
                0, this::predicate));
        animationData.addAnimationController(new AnimationController(this, "attackController",
                0, this::attackPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if(!QueenCatsConfig.enableHumanoidCowSounds){
            return ModSounds.HUMANOID_ENTITY_SILENT;
        }

        return ModSounds.HUMANOID_COW_AMBIENT;
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        if(!QueenCatsConfig.enableHumanoidCowSounds){
            return ModSounds.HUMANOID_ENTITY_SILENT;
        }

        return ModSounds.HUMANOID_COW_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if(!QueenCatsConfig.enableHumanoidCowSounds){
            return ModSounds.HUMANOID_ENTITY_SILENT;
        }

        return ModSounds.HUMANOID_COW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if(!QueenCatsConfig.enableHumanoidCowSounds){
            return ModSounds.HUMANOID_ENTITY_SILENT;
        }

        return ModSounds.HUMANOID_COW_DEATH;
    }

    protected SoundEvent getMilkingSound(){
        if(!QueenCatsConfig.enableHumanoidCowSounds){
            return ModSounds.HUMANOID_ENTITY_SILENT;
        }

        return ModSounds.HUMANOID_COW_MILK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15f, 1.0f);
    }

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public boolean isMilkableVariant(){
        HumanoidCowVariant variant = this.getVariant();

        if(variant != HumanoidCowVariant.MOOSHROOM || variant != HumanoidCowVariant.MOOBLOOM){
            return true;
        }

        return false;
    }

    public boolean isStewableVariant(){
        HumanoidCowVariant variant = this.getVariant();

        if(variant == HumanoidCowVariant.MOOSHROOM || variant == HumanoidCowVariant.MOOBLOOM){
            return true;
        }

        return false;
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

    public HumanoidCowVariant getVariant() {
        return HumanoidCowVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(HumanoidCowVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
