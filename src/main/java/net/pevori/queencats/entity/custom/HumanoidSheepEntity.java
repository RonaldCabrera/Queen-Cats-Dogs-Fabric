package net.pevori.queencats.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pevori.queencats.config.QueenCatsConfig;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.sound.ModSounds;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import static net.pevori.queencats.sound.ModSounds.soundEventByConfig;

public class HumanoidSheepEntity extends HumanoidAnimalEntity implements GeoEntity, Shearable {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    protected Item itemForTaming = ModItems.GOLDEN_WHEAT;
    protected Ingredient itemForHealing = Ingredient.ofItems(Items.WHEAT, ModItems.GOLDEN_WHEAT);
    protected Item itemForGrowth = ModItems.KEMOMIMI_POTION;

    private static final int MAX_GRASS_TIMER = 40;
    private static final TrackedData<Byte> COLOR = DataTracker.registerData(HumanoidSheepEntity.class, TrackedDataHandlerRegistry.BYTE);
    protected static final TrackedData<Boolean> SHEARED = DataTracker.registerData(HumanoidSheepEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public static final Map<DyeColor, ItemConvertible> DROPS =
            Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
                map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
                map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
                map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
                map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
                map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
                map.put(DyeColor.LIME, Blocks.LIME_WOOL);
                map.put(DyeColor.PINK, Blocks.PINK_WOOL);
                map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
                map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
                map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
                map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
                map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
                map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
                map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
                map.put(DyeColor.RED, Blocks.RED_WOOL);
                map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
            });
    private static final EnumMap<DyeColor, float[]> COLORS = Maps.newEnumMap((Map) Arrays.stream(DyeColor.values()).collect(Collectors.toMap((color) -> color, HumanoidSheepEntity::getDyedColor)));

    private int eatGrassTimer;
    private EatGrassGoal eatGrassGoal;

    protected HumanoidSheepEntity(EntityType<? extends HumanoidAnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.eatGrassGoal = new EatGrassGoal(this);
        this.goalSelector.add(5, this.eatGrassGoal);
        super.initGoals();
    }

    protected void mobTick() {
        this.eatGrassTimer = this.eatGrassGoal.getTimer();
        super.mobTick();
    }

    public void tickMovement() {
        if (this.getWorld().isClient) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }

        super.tickMovement();
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

    private PlayState predicate(AnimationState<HumanoidSheepEntity> animationState) {
        if (this.isSitting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidsheep.sitting", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidsheep.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then("animation.humanoidsheep.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private PlayState attackPredicate(AnimationState<HumanoidSheepEntity> state) {
        if (this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.humanoidsheep.attack", Animation.LoopType.PLAY_ONCE));
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
        return soundEventByConfig(QueenCatsConfig.enableHumanoidSheepSounds, ModSounds.HUMANOID_SHEEP_AMBIENT);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidSheepSounds, ModSounds.HUMANOID_SHEEP_EAT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidSheepSounds, ModSounds.HUMANOID_SHEEP_HURT);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return soundEventByConfig(QueenCatsConfig.enableHumanoidSheepSounds, ModSounds.HUMANOID_SHEEP_DEATH);
    }

    protected SoundEvent getShearSound() {
        if (QueenCatsConfig.enableHumanoidSheepSounds) {
            return ModSounds.HUMANOID_SHEEP_SHEAR;
        }

        return SoundEvents.ENTITY_SHEEP_SHEAR;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15f, 1.0f);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();

        if (isBreedingItem(itemStack)) {
            return super.interactMob(player, hand);
        }

        if (item instanceof DyeItem dyeItem && (!this.isTamed() || this.isOwner(player))) {
            DyeColor dyeColor = dyeItem.getColor();

            if (this.getColor() == dyeColor) {
                return ActionResult.PASS;
            }

            if (!player.getAbilities().creativeMode && !player.getWorld().isClient) {
                itemStack.decrement(1);
            }

            this.getWorld().playSoundFromEntity(player, this, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            this.setColor(dyeColor);

            return ActionResult.CONSUME;
        }

        if (isTamed() && this.isOwner(player) && !player.isSneaking() && !this.getWorld().isClient() && hand == Hand.MAIN_HAND) {
            setSit(!isSitting());
            return ActionResult.SUCCESS;
        }

        if (itemStack.isOf(Items.SHEARS)) {
            if (!this.getWorld().isClient && this.isShearable()) {
                this.sheared(SoundCategory.PLAYERS);
                this.emitGameEvent(GameEvent.SHEAR, player);
                itemStack.damage(1, player, (playerx) -> {
                    playerx.sendToolBreakStatus(hand);
                });

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.CONSUME;
            }
        } else {
            return super.interactMob(player, hand);
        }
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.dataTracker.get(COLOR) & 0xF);
    }

    protected DyeColor getChildColor(HumanoidSheepEntity mom, HumanoidSheepEntity otherMom) {
        var momDyeColor = mom.getColor();
        var otherMomDyeColor = otherMom.getColor();

        return this.getMixedColor(momDyeColor, otherMomDyeColor);
    }

    private DyeColor getMixedColor(DyeColor firstDyeColor, DyeColor secondDyeColor) {
        var world = this.getWorld();
        var craftingInventory = new CraftingInventory(new ScreenHandler(null, 0) {
            @Override
            public ItemStack quickMove(PlayerEntity player, int slot) {
                return ItemStack.EMPTY;
            }

            @Override public boolean canUse(PlayerEntity player) { return false; }
        }, 2, 1);

        craftingInventory.setStack(0, new ItemStack(DyeItem.byColor(firstDyeColor)));
        craftingInventory.setStack(1, new ItemStack(DyeItem.byColor(secondDyeColor)));

        var recipe = world.getRecipeManager()
                .getFirstMatch(RecipeType.CRAFTING, craftingInventory, world)
                .map(RecipeEntry::value).orElse(null);;

        if (recipe != null) {
            ItemStack result = recipe.craft(craftingInventory, world.getRegistryManager());

            if (result.getItem() instanceof DyeItem dyeItem){
                return dyeItem.getColor();
            }
        }

        // if there somehow is no combination, then select the color of one of the parents
        return world.random.nextBoolean()
                ? firstDyeColor
                : secondDyeColor;
    }


    public void setColor(DyeColor color) {
        byte b = this.dataTracker.get(COLOR);
        this.dataTracker.set(COLOR, (byte) (b & 0xF0 | color.getId() & 0xF));
    }

    @Contract(value = "_ -> new", pure = true)
    private static float @NotNull [] getDyedColor(DyeColor color) {
        if (color == DyeColor.WHITE) {
            return new float[]{0.9019608f, 0.9019608f, 0.9019608f};
        }
        float[] fs = color.getColorComponents();
        return new float[]{fs[0] * 0.75f, fs[1] * 0.75f, fs[2] * 0.75f};
    }

    public static float[] getRgbColor(DyeColor dyeColor) {
        return COLORS.get(dyeColor);
    }

    @Override
    public void sheared(SoundCategory shearedSoundCategory) {
        this.getWorld().playSoundFromEntity(null, this, this.getShearSound(), shearedSoundCategory, 1.0f, 1.0f);
        this.setSheared(true);
        int i = 1 + this.random.nextInt(3);
        for (int j = 0; j < i; ++j) {
            ItemEntity itemEntity = this.dropItem(DROPS.get(this.getColor()), 1);
            if (itemEntity == null) continue;
            itemEntity.setVelocity(itemEntity.getVelocity().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
        }
    }

    public boolean isShearable() {
        return this.isAlive() && !this.isSheared();
    }

    public boolean isSheared() {
        return this.dataTracker.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.dataTracker.set(SHEARED, sheared);
    }

    public void onEatingGrass() {
        super.onEatingGrass();
        this.setSheared(false);
        if (this.isBaby()) {
            this.growUp(60);
        }
    }

    public static DyeColor generateDefaultColor(Random random) {
        int i = random.nextInt(100);
        if (i < 5) {
            return DyeColor.BLACK;
        }
        if (i < 10) {
            return DyeColor.GRAY;
        }
        if (i < 15) {
            return DyeColor.LIGHT_GRAY;
        }
        if (i < 18) {
            return DyeColor.BROWN;
        }
        if (random.nextInt(500) == 0) {
            return DyeColor.PINK;
        }
        return DyeColor.WHITE;
    }

    /* TAMEABLE ENTITY */
    protected static final TrackedData<Boolean> SITTING = DataTracker.registerData(HumanoidSheepEntity.class,
            TrackedDataHandlerRegistry.BOOLEAN);

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void startGrowth() {
        var queenEntity = ModEntities.QUEEN_SHEEP.create(this.getWorld());
        queenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        queenEntity.setAiDisabled(this.isAiDisabled());
        queenEntity.setInventory(this.inventory);

        queenEntity.setColor(this.getColor());

        if (this.hasCustomName()) {
            queenEntity.setCustomName(this.getCustomName());
            queenEntity.setCustomNameVisible(this.isCustomNameVisible());
        }

        queenEntity.setPersistent();
        queenEntity.setOwnerUuid(this.getOwnerUuid());
        queenEntity.setTamed(true);
        queenEntity.setSitting(this.isSitting());
        this.getWorld().spawnEntity(queenEntity);
        this.discard();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sheared", this.dataTracker.get(SHEARED));
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putByte("Color", (byte) this.getColor().getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SHEARED, nbt.getBoolean("Sheared"));
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.setColor(DyeColor.byId(nbt.getByte("Color")));
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
        this.dataTracker.startTracking(SHEARED, false);
        this.dataTracker.startTracking(COLOR, (byte) 0);
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setColor(HumanoidSheepEntity.generateDefaultColor(world.getRandom()));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
}
