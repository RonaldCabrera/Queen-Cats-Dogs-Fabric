package net.pevori.queencats.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.pevori.queencats.entity.custom.HumanoidSheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin {
    @Shadow
    abstract DyeColor getColor();

    @Inject(method = "useOnEntity", at = @At("HEAD"), cancellable = true)
    private void dyeHumanoidSheep(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if(entity instanceof HumanoidSheepEntity humanoidSheepEntity) {
            DyeColor dyeItemColor = this.getColor();
            DyeColor currentSheepColor = humanoidSheepEntity.getColor();

            if(humanoidSheepEntity.isAlive() && currentSheepColor != dyeItemColor) {
                humanoidSheepEntity.getWorld().playSoundFromEntity(user, humanoidSheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if(!user.getWorld().isClient) {
                    humanoidSheepEntity.setColor(dyeItemColor);
                    stack.decrement(1);
                }

                info.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }
}
