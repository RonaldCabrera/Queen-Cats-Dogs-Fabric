package net.pevori.queencats;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.pevori.queencats.item.ModItemGroup;
import net.pevori.queencats.item.ModItems;
import net.pevori.queencats.screen.HumanoidAnimalScreen;
import net.pevori.queencats.screen.HumanoidAnimalScreenRegistries;
import net.pevori.queencats.util.ModRegistries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueenCats implements ModInitializer {
	public static final String MOD_ID = "queencats";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroup.registerItemGroup();
		ModItems.registerModItems();

		ModRegistries.registerQueenCats();
	}
}
