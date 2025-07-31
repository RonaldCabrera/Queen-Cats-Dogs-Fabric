package net.pevori.queencats.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.pevori.queencats.entity.custom.HumanoidAnimalEntity;

public class HumanoidAnimalScreenHandler extends ScreenHandler {
    private Inventory inventory;
    private int entityId;
    private HumanoidAnimalEntity entity;

    // Client ScreenHandler Initializer
    public HumanoidAnimalScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, (HumanoidAnimalEntity) MinecraftClient.getInstance().world.getEntityById(buf.readInt()));
        this.entityId = buf.readInt();

        this.inventory = new SimpleInventory(19);
        this.entity = (HumanoidAnimalEntity) MinecraftClient.getInstance().world.getEntityById(entityId);
        this.inventory.onOpen(playerInventory.player);
    }

    // Server ScreenHandler Initializer
    public HumanoidAnimalScreenHandler(int syncId, PlayerInventory playerInventory, HumanoidAnimalEntity entity) {
        super(HumanoidAnimalScreenRegistries.HUMANOID_ANIMAL_SCREEN_HANDLER, syncId);

        Inventory entityInventory = entity.getInventory();
        checkSize(entityInventory, 19);
        this.inventory = entityInventory;

        this.addSlot(getCustomArmorSlot());
        this.addHumanoidAnimalInventory();
        this.addPlayerInventory(playerInventory);
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
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public HumanoidAnimalEntity getEntity() {
        return entity;
    }

    public int getInventoryColumns() {
        return 6;
    }

    public Slot getCustomArmorSlot() {
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

    public boolean isValidArmor(ItemStack itemStack) {
        return itemStack.getItem() instanceof ArmorItem item && item.getSlotType() == EquipmentSlot.CHEST;
    }

    public void addHumanoidAnimalInventory() {
        int row, column;
        //The Humanoid Animal´s inventory
        for (row = 0; row < 3; ++row) {
            for (column = 0; column < getInventoryColumns(); ++column) {
                this.addSlot(new Slot(inventory, 1 + column + row * getInventoryColumns(), 62 + column * 18, 18 + row * 18));
            }
        }
    }

    public void addPlayerInventory(PlayerInventory playerInventory) {
        int row, column;

        //The player inventory
        for (row = 0; row < 3; ++row) {
            for (column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        //The player Hotbar
        for (row = 0; row < 9; ++row) {
            this.addSlot(new Slot(playerInventory, row, 8 + row * 18, 142));
        }
    }

    public HumanoidAnimalEntity getEntityServerSide(PacketByteBuf buf) {
        HumanoidAnimalEntity humanoidAnimal = (HumanoidAnimalEntity) MinecraftClient.getInstance().world.getEntityById(buf.readInt());
        this.entity = humanoidAnimal;
        return humanoidAnimal;
    }
}
