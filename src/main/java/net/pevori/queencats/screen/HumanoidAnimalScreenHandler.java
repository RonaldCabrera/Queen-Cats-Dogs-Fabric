package net.pevori.queencats.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.HumanoidAnimalEntity;

public class HumanoidAnimalScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private int entityId;
    private HumanoidAnimalEntity entity;

    public HumanoidAnimalScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(19));

        entityId = buf.readInt();
        this.entity = (HumanoidAnimalEntity) playerInventory.player.world.getEntityById(entityId);
    }

    public HumanoidAnimalScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(HumanoidAnimalScreenRegistries.HUMANOID_ANIMAL_SCREEN_HANDLER, syncId);

        checkSize(inventory, 19);
        this.inventory = inventory;

        inventory.onOpen(playerInventory.player);

        //This will add the slot to place the armor in the HumanoidAnimal inventory.
        Slot customSlot = getCustomArmorSlot();
        this.addSlot(customSlot);

        //This will place the slot in the correct locations for a 4x5 Grid. The slots exist on both server and client!
        //This will not render the background of the slots however, this is the Screens job
        int m, l;
        //The Humanoid Animal´s inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < getInventoryColumns(); ++l) {
                this.addSlot(new Slot(inventory, 1 + l + m * getInventoryColumns(), 62 + l * 18, 18 + m * 18));
            }
        }

        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }

    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.markDirty();

        this.inventory.onClose(player);
    }

    public HumanoidAnimalEntity getEntity(){
        return entity;
    }

    public int getInventoryColumns() {
        return 6;
    }

    // Generates the new armor slot trying to avoid null exceptions.
    public Slot getCustomArmorSlot(){
        return new Slot(inventory, 0, 8, 36) {
            public boolean canInsert(ItemStack stack) {
                return isValidArmor(stack);
            }

            public boolean isEnabled() {
                return entity.hasArmorSlot();
            }

            public int getMaxItemCount() {
                return 1;
            }
        };
    }

    public boolean isValidArmor(ItemStack itemStack){
        return Ingredient.ofItems(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE,
                Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE).test(itemStack);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        ItemStack itemStack = inventory.getStack(slotIndex);

        if(entity == null){
            entity = (HumanoidAnimalEntity) player.world.getEntityById(entityId);
        }

        if (slotIndex == 0 && actionType == SlotActionType.PICKUP){
            // If the slot was empty, equip the armor on the entity
            if(itemStack.isEmpty()){
                entity.unEquipArmor();
            }
            else {
                entity.equipArmor(itemStack);
            }
        }
    }
}
