package net.pevori.queencats.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;

public class HumanoidAnimalScreenRegistries {
    public static final Identifier HUMANOID_ANIMAL_SCREEN = new Identifier(QueenCats.MOD_ID, "humanoid_animal_screen");
    public static final Identifier TEXTURE = new Identifier(QueenCats.MOD_ID, "textures/gui/container/humanoid_animal.png");
    public static ScreenHandlerType<HumanoidAnimalScreenHandler> HUMANOID_ANIMAL_SCREEN_HANDLER;

    public static void registerScreenHandlers() {
        HUMANOID_ANIMAL_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(HUMANOID_ANIMAL_SCREEN, HumanoidAnimalScreenHandler::new);
    }

    public static void registerScreenRenderers(){
        HandledScreens.register(HUMANOID_ANIMAL_SCREEN_HANDLER, HumanoidAnimalScreen::new);
    }
}