package net.pevori.queencats;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.pevori.queencats.config.QueenCatsConfig;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.*;
import net.pevori.queencats.item.ModItemGroup;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.screen.ModScreenHandlers;
import net.pevori.queencats.sound.ModSounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueenCats implements ModInitializer {
	public static final String MOD_ID = "queencats";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroup.registerItemGroups();

		ModItems.registerModItems();

		ModSounds.registerSounds();

		ModEntities.registerModEntities();

		ModScreenHandlers.registerScreenHandlers();

		MidnightConfig.init(MOD_ID, QueenCatsConfig.class);

		FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_CAT, QueenCatEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_CAT, PrincessCatEntity.setAttributes());

		FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_DOG, QueenDogEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_DOG, PrincessDogEntity.setAttributes());

		FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_BUNNY, QueenDogEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_BUNNY, PrincessDogEntity.setAttributes());

		FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_COW, QueenCowEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_COW, PrincessCowEntity.setAttributes());

		FabricDefaultAttributeRegistry.register(ModEntities.QUEEN_SHEEP, QueenSheepEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PRINCESS_SHEEP, PrincessSheepEntity.setAttributes());
	}
}
