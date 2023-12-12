package net.pevori.queencats.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.pevori.queencats.entity.custom.HumanoidAnimalEntity;

public class HumanoidAnimalScreen extends HandledScreen<HumanoidAnimalScreenHandler> {
    private final HumanoidAnimalEntity entity;

    public HumanoidAnimalScreen(HumanoidAnimalScreenHandler handler, PlayerInventory inventory, Text text) {
        super(handler, inventory, text);
        this.entity = handler.getEntity();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, HumanoidAnimalScreenRegistries.TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // TODO: Add weapon slot eventually.
        /*if (this.entity.hasWeaponSlot()) {
            this.drawTexture(matrices, i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
        }*/

        // Draws the background of the inventory.
        context.drawTexture(HumanoidAnimalScreenRegistries.TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Draws the entity inventory slots.
        context.drawTexture(HumanoidAnimalScreenRegistries.TEXTURE, x + 61, y + 17, 0, this.backgroundHeight, 6 * 18, 54);

        // Draws the player inventory and hotbar slots.
        context.drawTexture(HumanoidAnimalScreenRegistries.TEXTURE, x + 7, y + 35, 0, this.backgroundHeight + 54, 18, 18);

        // Draws the entity render in the black box.
        InventoryScreen.drawEntity(context, x + 20, y + 18, x + 68, y + 70, 20, 0.25f, mouseX, mouseY, this.entity);    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 14;
    }
}
