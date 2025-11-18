package net.pevori.queencats.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.network.packet.HumanoidScreenHandlerPacketS2C;
import net.pevori.queencats.screen.custom.HumanoidAnimalScreenHandler;

public class ModScreenHandlers {
    public static final Identifier HUMANOID_ANIMAL_SCREEN = Identifier.of(QueenCats.MOD_ID, "humanoid_animal_screen");

    public static ScreenHandlerType<HumanoidAnimalScreenHandler> HUMANOID_ANIMAL_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, HUMANOID_ANIMAL_SCREEN,
                    new ExtendedScreenHandlerType<>(HumanoidAnimalScreenHandler::new, HumanoidScreenHandlerPacketS2C.PACKET_CODEC));

    public static void registerScreenHandlers() {
        QueenCats.LOGGER.info("Registering Screen Handlers for " + QueenCats.MOD_ID);
    }
}
