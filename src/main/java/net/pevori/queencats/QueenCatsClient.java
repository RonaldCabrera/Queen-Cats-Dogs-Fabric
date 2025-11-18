package net.pevori.queencats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.client.*;
import net.pevori.queencats.screen.ModScreenHandlers;
import net.pevori.queencats.screen.custom.HumanoidAnimalScreen;

@Environment(EnvType.CLIENT)
public class QueenCatsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.QUEEN_CAT, QueenCatRenderer::new);
        EntityRendererRegistry.register(ModEntities.PRINCESS_CAT, PrincessCatRenderer::new);

        EntityRendererRegistry.register(ModEntities.QUEEN_DOG, QueenDogRenderer::new);
        EntityRendererRegistry.register(ModEntities.PRINCESS_DOG, PrincessDogRenderer::new);

        EntityRendererRegistry.register(ModEntities.QUEEN_BUNNY, QueenBunnyRenderer::new);
        EntityRendererRegistry.register(ModEntities.PRINCESS_BUNNY, PrincessBunnyRenderer::new);

        EntityRendererRegistry.register(ModEntities.QUEEN_COW, QueenCowRenderer::new);
        EntityRendererRegistry.register(ModEntities.PRINCESS_COW, PrincessCowRenderer::new);

        EntityRendererRegistry.register(ModEntities.QUEEN_SHEEP, QueenSheepRenderer::new);
        EntityRendererRegistry.register(ModEntities.PRINCESS_SHEEP, PrincessSheepRenderer::new);

        HandledScreens.register(ModScreenHandlers.HUMANOID_ANIMAL_SCREEN_HANDLER, HumanoidAnimalScreen::new);
    }
}
