package net.pevori.queencats.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;

public class ModItemGroup {
    public static ItemGroup QUEENCATS;

    public static void registerItemGroup(){
        QUEENCATS = FabricItemGroup.builder(new Identifier(QueenCats.MOD_ID, "queencats"))
                .displayName(Text.literal("Queen Cats Item Group"))
                .icon(() -> new ItemStack(ModItems.GOLDEN_FISH)).build();
    }
}
