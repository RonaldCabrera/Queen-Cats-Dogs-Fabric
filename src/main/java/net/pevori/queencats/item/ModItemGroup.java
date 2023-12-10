package net.pevori.queencats.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;

public class ModItemGroup {
    public static ItemGroup QUEENCATS = Registry.register(Registries.ITEM_GROUP, new Identifier(QueenCats.MOD_ID, "queencats"),
            FabricItemGroup.builder().displayName(Text.literal("Queen Cats & Dogs"))
                    .icon(() -> new ItemStack(ModItems.GOLDEN_FISH)).entries((displayContext, entries) -> {
                        entries.add(ModItems.GOLDEN_FISH);
                        entries.add(ModItems.GOLDEN_BONE);
                        entries.add(ModItems.GOLDEN_WHEAT);
                        entries.add(ModItems.KEMOMIMI_POTION);

                        entries.add(ModItems.QUEEN_CAT_SPAWN_EGG);
                        entries.add(ModItems.PRINCESS_CAT_SPAWN_EGG);
                        entries.add(ModItems.QUEEN_DOG_SPAWN_EGG);
                        entries.add(ModItems.PRINCESS_DOG_SPAWN_EGG);
                        entries.add(ModItems.QUEEN_BUNNY_SPAWN_EGG);
                        entries.add(ModItems.PRINCESS_BUNNY_SPAWN_EGG);
                        entries.add(ModItems.QUEEN_COW_SPAWN_EGG);
                        entries.add(ModItems.PRINCESS_COW_SPAWN_EGG);
                    }).build());

    public static void registerItemGroups(){

    }
}
