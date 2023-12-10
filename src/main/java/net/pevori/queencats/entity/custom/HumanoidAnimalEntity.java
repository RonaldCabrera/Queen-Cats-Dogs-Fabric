package net.pevori.queencats.entity.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.pevori.queencats.screen.HumanoidAnimalScreenHandler;
import org.jetbrains.annotations.Nullable;

public abstract class HumanoidAnimalEntity extends TameableEntity implements ExtendedScreenHandlerFactory, InventoryChangedListener {
    protected static final String INVENTORY_KEY = "Humanoid_Animal_Inventory";
    protected static final String ARMOR_KEY = "Humanoid_Animal_Armor_Item";
    protected static final String SLOT_KEY = "Humanoid_Animal_Inventory_Slot";
    protected SimpleInventory inventory;

    protected Ingredient equippableArmor = Ingredient.ofItems(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE,
            Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);

    protected HumanoidAnimalEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.updateInventory();
    }

    @Override
    protected void initGoals() {
        // Allows pathfinding through doors
        ((MobNavigation) this.getNavigation()).canEnterOpenDoors();
        ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(true);

        // Allows them to actually open doors to walk through them, just like villagers.
        this.goalSelector.add(1, new LongDoorInteractGoal(this, true));
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (target instanceof CreeperEntity || target instanceof GhastEntity) {
            return false;
        }
        if (target instanceof HumanoidAnimalEntity humanoidAnimalEntity) {
            return !humanoidAnimalEntity.isTamed() || humanoidAnimalEntity.getOwner() != owner;
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!this.world.isClient && this.isOwner(player) && this.hasArmorSlot() && this.isValidArmor(itemStack) && !this.hasArmorInSlot()) {
            this.equipArmor(player, itemStack);
            return ActionResult.success(this.world.isClient);
        }

        if (!this.world.isClient && this.isOwner(player) && player.isSneaking()) {
            player.openHandledScreen(this);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    public boolean hasArmorSlot() {
        return !isBaby();
    }

    public boolean hasArmorInSlot() {
        return this.hasStackEquipped(EquipmentSlot.CHEST);
    }

    public boolean isValidArmor(ItemStack itemStack){
        return equippableArmor.test(itemStack);
    }

    public void equipArmor(PlayerEntity player, ItemStack stack) {
        if(!this.world.isClient()){
            if (this.isValidArmor(stack)) {
                this.inventory.setStack(0, stack.copy());
                this.playSound(stack.getEquipSound(), 0.5F, 1.0F);
                equipArmor(stack);
                this.inventory.markDirty();

                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
            }
        }
    }

    public void equipArmor(ItemStack stack) {
        if(!this.world.isClient()){
            if (this.isValidArmor(stack)) {
                this.equipStack(EquipmentSlot.CHEST, stack);
                this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
            }
        }
    }

    public int getInventorySize() {
        return 19;
    }

    protected void updateInventory() {
        var previousInventory = this.inventory;
        this.inventory = new SimpleInventory(this.getInventorySize());
        if (previousInventory != null) {
            previousInventory.removeListener(this);
            int maxSize = Math.min(previousInventory.size(), this.inventory.size());

            for (int slot = 0; slot < maxSize; ++slot) {
                var stack = previousInventory.getStack(slot);
                if (!stack.isEmpty()) {
                    this.inventory.setStack(slot, stack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.syncInventoryToFlags();
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory != null) {
            for(int i = 1; i < this.inventory.size(); ++i) {
                ItemStack itemStack = this.inventory.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
                    this.dropStack(itemStack);
                }
            }
        }
    }

    /**
     * Syncs the flags with the inventory.
     */
    public void syncInventoryToFlags() {
        if (!this.getWorld().isClient()) {
            this.equipArmor(this.inventory.getStack(0));
        }
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        boolean previouslyEquipped = this.hasArmorInSlot();
        this.syncInventoryToFlags();

        if (this.age > 20 && !previouslyEquipped && this.hasArmorInSlot()) {
            SoundEvent armorInSlotEquipSound = this.inventory.getStack(0).getEquipSound();
            this.playSound(armorInSlotEquipSound, 0.5F, 1.0F);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NbtList nbtList = new NbtList();

        // Writes the armor slot (slot 0)
        if (!this.inventory.getStack(0).isEmpty()) {
            nbt.put(ARMOR_KEY, this.inventory.getStack(0).writeNbt(new NbtCompound()));
        }

        // Writes the rest of the inventory (slot 2 to 18th)
        for(int i = 1; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte(SLOT_KEY, (byte)i);
                itemStack.writeNbt(nbtCompound);
                nbtList.add(nbtCompound);
            }
        }

        nbt.put(INVENTORY_KEY, nbtList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        NbtList nbtList = nbt.getList(INVENTORY_KEY, NbtElement.COMPOUND_TYPE);

        // Reads the armor slot (slot 0)
        if (nbt.contains(ARMOR_KEY, NbtElement.COMPOUND_TYPE)) {
            ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound(ARMOR_KEY));

            if (!itemStack.isEmpty() && this.isValidArmor(itemStack)) {
                this.inventory.setStack(0, itemStack);
                this.equipArmor(itemStack);
            }
        }

        // Reads the rest of the inventory (slot 2 to 18th)
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte(SLOT_KEY) & 255;
            if (j >= 1 && j < this.inventory.size()) {
                this.inventory.setStack(j, ItemStack.fromNbt(nbtCompound));
            }
        }
    }

    public Inventory getInventory(){
        if(this.inventory == null){
            return new SimpleInventory(this.getInventorySize());
        }

        return this.inventory;
    }

    public void setInventory(Inventory inventory){
        for(int i = 0; i < inventory.size(); i++){
            this.inventory.setStack(i, inventory.getStack(i));
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(this.getId());
        buf.writeInt(this.getId());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HumanoidAnimalScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return super.getDisplayName();
    }
}
