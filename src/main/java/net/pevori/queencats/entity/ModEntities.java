package net.pevori.queencats.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.*;

public class ModEntities {
    public static final float queenSizeHeight = 1.9f;
    public static final float queenSizeWidth = 0.5f;
    public static final float princessSizeHeight = 1.5f;
    public static final float princessSizeWidth = 0.45f;

    public static final EntityType<QueenCatEntity> QUEEN_CAT = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "queen_cat"),
            EntityType.Builder.create(QueenCatEntity::new, SpawnGroup.CREATURE)
                .dimensions(queenSizeWidth, queenSizeHeight).build());

    public static final EntityType<PrincessCatEntity> PRINCESS_CAT = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "princess_cat"),
            EntityType.Builder.create(PrincessCatEntity::new, SpawnGroup.CREATURE)
                .dimensions(princessSizeWidth, princessSizeHeight).build());

    public static final EntityType<QueenDogEntity> QUEEN_DOG = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "queen_dog"),
            EntityType.Builder.create(QueenDogEntity::new, SpawnGroup.CREATURE)
                .dimensions(queenSizeWidth, queenSizeHeight).build());

    public static final EntityType<PrincessDogEntity> PRINCESS_DOG = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "princess_dog"),
            EntityType.Builder.create(PrincessDogEntity::new, SpawnGroup.CREATURE)
                .dimensions(princessSizeWidth, princessSizeHeight).build());

    public static final EntityType<QueenBunnyEntity> QUEEN_BUNNY = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "queen_bunny"),
            EntityType.Builder.create(QueenBunnyEntity::new, SpawnGroup.CREATURE)
                .dimensions(queenSizeWidth, queenSizeHeight).build());

    public static final EntityType<PrincessBunnyEntity> PRINCESS_BUNNY = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "princess_bunny"),
            EntityType.Builder.create(PrincessBunnyEntity::new, SpawnGroup.CREATURE)
                .dimensions(princessSizeWidth, princessSizeHeight).build());

    public static final EntityType<QueenCowEntity> QUEEN_COW = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "queen_cow"),
            EntityType.Builder.create(QueenCowEntity::new, SpawnGroup.CREATURE)
                    .dimensions(queenSizeWidth, queenSizeHeight).build());

    public static final EntityType<PrincessCowEntity> PRINCESS_COW = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "princess_cow"),
            EntityType.Builder.create(PrincessCowEntity::new, SpawnGroup.CREATURE)
                    .dimensions(princessSizeWidth, princessSizeHeight).build());

    public static final EntityType<QueenSheepEntity> QUEEN_SHEEP = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "queen_sheep"),
            EntityType.Builder.create(QueenSheepEntity::new, SpawnGroup.CREATURE)
                    .dimensions(queenSizeWidth, queenSizeHeight).build());

    public static final EntityType<PrincessSheepEntity> PRINCESS_SHEEP = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(QueenCats.MOD_ID, "princess_sheep"),
            EntityType.Builder.create(PrincessSheepEntity::new, SpawnGroup.CREATURE)
                    .dimensions(princessSizeWidth, princessSizeHeight).build());

    public static void registerModEntities() {
        QueenCats.LOGGER.info("Registering Mod Entities for " + QueenCats.MOD_ID);
    }
}
