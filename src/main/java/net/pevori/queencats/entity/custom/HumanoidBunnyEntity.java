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
import net.pevori.queencats.entity.variant.HumanoidBunnyVariant;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import static net.pevori.queencats.sound.ModSounds.soundEventByConfig;

public class HumanoidBunnyEntity extends HumanoidAnimalEntity implements GeoEntity {
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    protected Item itemForTaming = ModItems.GOLDEN_WHEAT;
    protected Ingredient itemForHealing = Ingredient.ofItems(Items.CARROT, Items.WHEAT, ModItems.GOLDEN_WHEAT, Items.GOLDEN_CARROT);
    protected Item itemForGrowth = ModItems.KEMOMIMI_POTION;
    public static final String pekoSan = "pekora";

    protected HumanoidBunnyEntity(EntityType<? extends HumanoidAnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (this.hasStackEquipped(EquipmentSlot.CHEST)) {
            this.dropStack(getEquippedStack(EquipmentSlot.CHEST));
        }
        super.onDeath(source);
    }

    public boolean isAlmond(){
        String s = Formatting.strip(this.getName().getString());
        return (s != null && s.toLowerCase().contains(pekoSan));
    }

    public void startGrowth() {
        HumanoidBunnyVariant variant = this.getVariant();
        QueenBunnyEntity queenBunnyEntity = ModEntities.QUEEN_BUNNY.create(world);
        queenBunnyEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        queenBunnyEntity.setAiDisabled(this.isAiDisabled());
        queenBunnyEntity.setInventory(this.inventory);

        queenBunnyEntity.setVariant(variant);

        if (this.hasCustomName()) {
            queenBunnyEntity.setCustomName(this.getCustomName());
            queenBunnyEntity.setCustomNameVisible(this.isCustomNameVisible());
        }

        queenBunnyEntity.setPersistent();
        queenBunnyEntity.setOwnerUuid(this.getOwnerUuid());
        queenBunnyEntity.setTamed(true);
        queenBunnyEntity.setSitting(this.isSitting());
        world.spawnEntity(queenBunnyEntity);
        this.discard();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == ModItems.KEMOMIMI_POTION;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidBunnySounds, ModSounds.HUMANOID_BUNNY_AMBIENT);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidBunnySounds, ModSounds.HUMANOID_BUNNY_EAT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidBunnySounds, ModSounds.HUMANOID_BUNNY_HURT);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidBunnySounds, ModSounds.HUMANOID_BUNNY_DEATH);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_RABBIT_JUMP, 0.15f, 1.0f);
    }

    private PlayState predicate(AnimationState animationState) {
        if (this.isSitting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidbunny.sitting", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidbunny.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidbunny.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.humanoidbunny.attack", Animation.LoopType.PLAY_ONCE));
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

    /* TAMEABLE ENTITY */
    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidBunnyEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
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

    /* VARIANTS */
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(HumanoidBunnyEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public HumanoidBunnyVariant getVariant() {
        return HumanoidBunnyVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(HumanoidBunnyVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }
}
