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
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.pevori.queencats.config.QueenCatsConfig;
import net.pevori.queencats.entity.variant.HumanoidCatVariant;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;

public class HumanoidCatEntity extends HumanoidAnimalEntity implements GeoEntity {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    protected final Item itemForTaming = ModItems.GOLDEN_FISH;
    protected final Ingredient itemForHealing = Ingredient.ofItems(Items.COD, Items.SALMON, ModItems.GOLDEN_FISH);

    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidCatEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(HumanoidCatEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    protected HumanoidCatEntity(EntityType<? extends HumanoidAnimalEntity> entityType, World world) {
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
                        this.setVariant(HumanoidCatVariant.BLACK);
                    } else if (dyeColor == DyeColor.WHITE) {
                        this.setVariant(HumanoidCatVariant.WHITE);
                    } else if (dyeColor == DyeColor.ORANGE) {
                        this.setVariant(HumanoidCatVariant.CALICO);
                    } else if (dyeColor == DyeColor.GRAY) {
                        this.setVariant(HumanoidCatVariant.CALLAS);
                    }

                    itemStack.decrementUnlessCreative(1, player);
                    this.setPersistent();

                    return ActionResult.success(this.getWorld().isClient());
                }
                else if ((itemForHealing.test(itemStack)) && this.getHealth() < getMaxHealth()) {
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

    private PlayState predicate(AnimationState<HumanoidCatEntity> animationState) {
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

    private PlayState attackPredicate(AnimationState<HumanoidCatEntity> state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.humanoidcat.attack", Animation.LoopType.PLAY_ONCE));
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
        return ModSounds.soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_AMBIENT);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return ModSounds.soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_EAT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_HURT);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.soundEventByConfig(QueenCatsConfig.enableHumanoidCatSounds, ModSounds.HUMANOID_CAT_DEATH);
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
    public HumanoidCatVariant getVariant() {
        return HumanoidCatVariant.byId(this.getTypeVariant() & 255);
    }

    protected int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(HumanoidCatVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public boolean hasHoloNameEasterEgg() {
        return (this.hasCustomName() && this.getName().getString().toLowerCase().contains("okayu"));
    }
}
