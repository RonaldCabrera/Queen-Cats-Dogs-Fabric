package net.pevori.queencats.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.custom.*;

public class ModEntities {
    public static final float queenSizeHeight = 1.9f;
    public static final float queenSizeWidth = 0.5f;
    public static final float princessSizeHeight = 1.5f;
    public static final float princessSizeWidth = 0.45f;

    public static final EntityType<QueenCatEntity> QUEEN_CAT = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(QueenCats.MOD_ID, "queen_cat"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, QueenCatEntity::new)
                .dimensions(EntityDimensions.fixed(queenSizeWidth, queenSizeHeight)).build());

    public static final EntityType<PrincessCatEntity> PRINCESS_CAT = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(QueenCats.MOD_ID, "princess_cat"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PrincessCatEntity::new)
                .dimensions(EntityDimensions.fixed(princessSizeWidth, princessSizeHeight)).build());

    public static final EntityType<QueenDogEntity> QUEEN_DOG = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(QueenCats.MOD_ID, "queen_dog"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, QueenDogEntity::new)
                .dimensions(EntityDimensions.fixed(queenSizeWidth, queenSizeHeight)).build());

    public static final EntityType<PrincessDogEntity> PRINCESS_DOG = Registry.register(
        Registry.ENTITY_TYPE, new Identifier(QueenCats.MOD_ID, "princess_dog"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PrincessDogEntity::new)
                .dimensions(EntityDimensions.fixed(princessSizeWidth, princessSizeHeight)).build());

    public static final EntityType<QueenBunnyEntity> QUEEN_BUNNY = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(QueenCats.MOD_ID, "queen_bunny"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, QueenBunnyEntity::new)
                    .dimensions(EntityDimensions.fixed(queenSizeWidth, queenSizeHeight)).build());

    public static final EntityType<PrincessBunnyEntity> PRINCESS_BUNNY = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(QueenCats.MOD_ID, "princess_bunny"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, PrincessBunnyEntity::new)
                    .dimensions(EntityDimensions.fixed(princessSizeWidth, princessSizeHeight)).build());
}
