package net.pevori.queencats.util;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.*;
import net.pevori.queencats.screen.HumanoidAnimalScreenRegistries;
import net.pevori.queencats.sound.ModSounds;

public class ModRegistries {
    public static void registerQueenCats() {
        registerAttributes();
        registerSounds();
        registerScreens();
    }

    private static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_CAT, QueenCatEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_CAT, PrincessCatEntity.setAttributes());

        FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_DOG, QueenDogEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_DOG, PrincessDogEntity.setAttributes());

        FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_BUNNY, QueenDogEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_BUNNY, PrincessDogEntity.setAttributes());

        FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_COW, QueenCowEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_COW, PrincessCowEntity.setAttributes());
    }

    private static void registerSounds(){
        ModSounds.bootSounds();
    }

    private static void registerScreens(){
        HumanoidAnimalScreenRegistries.registerScreenHandlers();
    }
}
