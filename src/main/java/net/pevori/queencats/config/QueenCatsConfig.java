package net.pevori.queencats.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class QueenCatsConfig extends MidnightConfig {
    @Entry(name = "queencats.midnightconfig.enable_humanoid_cat_sounds")
    public static boolean enableHumanoidCatSounds = true;
    @Entry(name = "queencats.midnightconfig.enable_humanoid_dog_sounds")
    public static boolean enableHumanoidDogSounds = true;
    @Entry(name = "queencats.midnightconfig.enable_humanoid_bunny_sounds")
    public static boolean enableHumanoidBunnySounds = true;
    @Entry(name = "queencats.midnightconfig.enable_humanoid_cow_sounds")
    public static boolean enableHumanoidCowSounds = true;
}
