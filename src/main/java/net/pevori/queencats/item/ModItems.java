package net.pevori.queencats.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.ModEntities;

public class ModItems {
    public static final Item GOLDEN_FISH = registerItem("golden_fish", 
        new GlintedItem(new FabricItemSettings().food(ModFoodComponents.GOLDEN_FISH))
    );

    public static final Item GOLDEN_BONE = registerItem("golden_bone", 
        new GlintedItem(new FabricItemSettings())
    );

    public static final Item GOLDEN_WHEAT = registerItem("golden_wheat",
            new GlintedItem(new FabricItemSettings())
    );

    public static final Item KEMOMIMI_POTION = registerItem("kemomimi_potion", 
        new GlintedItem(new FabricItemSettings())
    );

    public static final Item QUEEN_CAT_SPAWN_EGG = registerItem("queen_cat_spawn_egg",
        new SpawnEggItem(ModEntities.QUEEN_CAT, 0xF3F7FA, 0xB9EDFE,
        new FabricItemSettings())
    );

    public static final Item PRINCESS_CAT_SPAWN_EGG = registerItem("princess_cat_spawn_egg",
        new SpawnEggItem(ModEntities.PRINCESS_CAT, 0xF3F7FA, 0xB9EDFE,
        new FabricItemSettings())
    );

    public static final Item QUEEN_DOG_SPAWN_EGG = registerItem("queen_dog_spawn_egg",
        new SpawnEggItem(ModEntities.QUEEN_DOG, 0xF3F7FA, 0x844204,
        new FabricItemSettings())
    );

    public static final Item PRINCESS_DOG_SPAWN_EGG = registerItem("princess_dog_spawn_egg",
        new SpawnEggItem(ModEntities.PRINCESS_DOG, 0xF3F7FA, 0x844204,
        new FabricItemSettings())
    );

    public static final Item QUEEN_BUNNY_SPAWN_EGG = registerItem("queen_bunny_spawn_egg",
            new SpawnEggItem(ModEntities.QUEEN_BUNNY, 0xF3F7FA, 0xA020F0,
                    new FabricItemSettings())
    );

    public static final Item PRINCESS_BUNNY_SPAWN_EGG = registerItem("princess_bunny_spawn_egg",
            new SpawnEggItem(ModEntities.PRINCESS_BUNNY, 0xF3F7FA, 0xDB68ED,
                    new FabricItemSettings())
    );

    public static final Item QUEEN_COW_SPAWN_EGG = registerItem("queen_cow_spawn_egg",
            new SpawnEggItem(ModEntities.QUEEN_COW, 0xF3F7FA, 0x403424,
                    new FabricItemSettings())
    );

    public static final Item PRINCESS_COW_SPAWN_EGG = registerItem("princess_cow_spawn_egg",
            new SpawnEggItem(ModEntities.PRINCESS_COW, 0xF3F7FA, 0x1D140E,
                    new FabricItemSettings())
    );

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(QueenCats.MOD_ID, name), item);
    }

    private static Item registerItem(String name, GlintedItem item){
        return Registry.register(Registries.ITEM, new Identifier(QueenCats.MOD_ID, name), item);
    }

    public static void registerModItems(){
        QueenCats.LOGGER.info("Registering Mod Items for " + QueenCats.MOD_ID);
    }

}
