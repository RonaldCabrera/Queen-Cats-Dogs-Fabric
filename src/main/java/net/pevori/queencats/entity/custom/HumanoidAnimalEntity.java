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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.screen.HumanoidAnimalScreenHandler;
import org.jetbrains.annotations.Nullable;

public abstract class HumanoidAnimalEntity extends TameableEntity implements ExtendedScreenHandlerFactory, InventoryChangedListener {
    protected static final String INVENTORY_KEY = "Humanoid_Animal_Inventory";
    protected static final String ARMOR_KEY = "Humanoid_Animal_Armor_Item";
    protected static final String SLOT_KEY = "Humanoid_Animal_Inventory_Slot";
    protected Inventory inventory;

    protected Ingredient equippableArmor = Ingredient.ofItems(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE,
            Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);

    protected HumanoidAnimalEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.inventory = new SimpleInventory(19);
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!this.world.isClient &&  this.isOwner(player) && !this.isBaby() && this.hasArmorSlot() && this.isEquippableArmor(itemStack) && !this.hasArmorInSlot()) {
            this.equipArmor(player, itemStack);
            this.equipArmor(itemStack);
            return ActionResult.success(this.world.isClient);
        }

        if (!this.world.isClient && this.isOwner(player) && player.isSneaking()) {
            player.openHandledScreen(this);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (target instanceof CreeperEntity || target instanceof GhastEntity) {
            return false;
        }
        if (target instanceof HumanoidAnimalEntity) {
            HumanoidAnimalEntity humanoidAnimalEntity = (HumanoidAnimalEntity) target;
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

    public boolean hasArmorSlot() {
        return !isBaby();
    }

    public boolean hasArmorInSlot() {
        return this.hasStackEquipped(EquipmentSlot.CHEST);
    }

    public boolean isEquippableArmor(ItemStack itemStack){
        return equippableArmor.test(itemStack);
    }

    public void equipArmor(PlayerEntity player, ItemStack stack) {
        if (this.isEquippableArmor(stack)) {
            this.inventory.setStack(0, new ItemStack(stack.getItem()));
            this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
            this.inventory.markDirty();

            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
    }

    public void equipArmor(ItemStack stack) {
        if (this.isEquippableArmor(stack)) {
            this.equipStack(EquipmentSlot.CHEST, stack);
            this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
        }
    }

    public void unEquipArmor(){
        this.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory != null) {
            for(int i = 0; i < this.inventory.size(); ++i) {
                ItemStack itemStack = this.inventory.getStack(i);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
                    this.dropStack(itemStack);
                }
            }
        }
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        this.inventory = sender;
    }

    protected void logInventory(String loggerTitle){
        QueenCats.LOGGER.info(loggerTitle);
        for(int i = 0; i < this.inventory.size(); ++i) {
            QueenCats.LOGGER.info("Index: "+ i + ", Item: " + this.inventory.getStack(i).getItem().toString());
        }
        QueenCats.LOGGER.info("----------------------------------------------");
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

            if (!itemStack.isEmpty() && this.isEquippableArmor(itemStack)) {
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

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        //Sends the entity id to pick it up later in the screenHandler and get back the entity reference.
        int entityId = getId();

        buf.writeInt(entityId);
    }

    public void setInventory(Inventory inventory){
        for(int i = 0; i < inventory.size(); i++){
            this.inventory.setStack(i, inventory.getStack(i));
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        //We provide this to the screenHandler as our class Implements Inventory
        //Only the Server has the Inventory at the start, this will be synced to the client in the ScreenHandler
        return new HumanoidAnimalScreenHandler(syncId, playerInventory, inventory);
    }

    @Override
    public Text getDisplayName() {
        return super.getDisplayName();
    }
}