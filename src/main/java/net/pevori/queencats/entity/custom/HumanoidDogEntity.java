package net.pevori.queencats.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.pevori.queencats.config.QueenCatsConfig;
import net.pevori.queencats.entity.variant.HumanoidDogVariant;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

import static net.pevori.queencats.sound.ModSounds.soundEventByConfig;

public class HumanoidDogEntity extends HumanoidAnimalEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    protected final Item itemForTaming = ModItems.GOLDEN_BONE;

    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidDogEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(HumanoidDogEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    protected HumanoidDogEntity(EntityType<? extends HumanoidAnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();

        if (this.isTamed()) {
            if (this.isOwner(player)){
                if (isBreedingItem(itemStack) && !player.isSneaking()) {
                    return super.interactMob(player, hand);
                }
                else if (item instanceof DyeItem && !player.isSneaking()) {
                    DyeColor dyeColor = ((DyeItem) item).getColor();
                    if (dyeColor == DyeColor.BLACK) {
                        this.setVariant(HumanoidDogVariant.HUSKY);
                    } else if (dyeColor == DyeColor.WHITE) {
                        this.setVariant(HumanoidDogVariant.SHIRO);
                    } else if (dyeColor == DyeColor.PINK) {
                        this.setVariant(HumanoidDogVariant.CREAM);
                    } else if (dyeColor == DyeColor.GRAY) {
                        this.setVariant(HumanoidDogVariant.GRAY);
                    }

                    itemStack.decrementUnlessCreative(1, player);
                    this.setPersistent();

                    return ActionResult.success(this.getWorld().isClient());
                }
                else if ((isMeatItem(itemStack)) && this.getHealth() < getMaxHealth()) {
                    itemStack.decrementUnlessCreative(1, player);

                    if (!this.getWorld().isClient()) {
                        this.eat(player, hand, itemStack);
                        this.heal(10.0f);
                    }

                    return ActionResult.success(this.getWorld().isClient());
                } else if (!player.isSneaking() && !this.getWorld().isClient() && hand == Hand.MAIN_HAND) {
                    setSit(!isSitting());
                    return ActionResult.SUCCESS;
                }
            }
        }
        else if (item == itemForTaming) {
            if (!this.getWorld().isClient()) {
                this.eat(player, hand, itemStack);
                this.tryTame(player);
                this.setPersistent();
            }

            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    private PlayState predicate(AnimationState<HumanoidDogEntity> animationState) {
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

    private PlayState attackPredicate(AnimationState<HumanoidDogEntity> state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.humanoiddog.attack", Animation.LoopType.PLAY_ONCE));
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
        if(this.isAttacking()) {
            return soundEventByConfig(QueenCatsConfig.enableHumanoidDogSounds, ModSounds.HUMANOID_DOG_ANGRY);
        }

        return soundEventByConfig(QueenCatsConfig.enableHumanoidDogSounds, ModSounds.HUMANOID_DOG_AMBIENT);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidDogSounds, ModSounds.HUMANOID_DOG_EAT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidDogSounds, ModSounds.HUMANOID_DOG_HURT);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidDogSounds, ModSounds.HUMANOID_DOG_DEATH);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15f, 1.0f);
    }

    @Override
    protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
        if (this.isBreedingItem(stack)) {
            this.playSound(getEatSound(stack), 1.0F, 1.0F);
        }

        super.eat(player, hand, stack);
    }

    /* TAMEABLE ENTITY */
    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public boolean isMeatItem(ItemStack stack) {
        return stack.isIn(ItemTags.WOLF_FOOD);
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
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SITTING, false);
        builder.add(DATA_ID_TYPE_VARIANT, 0);
    }

    /* VARIANTS */
    public HumanoidDogVariant getVariant() {
        return HumanoidDogVariant.byId(this.getTypeVariant() & 255);
    }

    protected int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(HumanoidDogVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public boolean hasHoloNameEasterEgg() {
        return (this.hasCustomName() && this.getName().getString().toLowerCase().contains("korone"));
    }
}
