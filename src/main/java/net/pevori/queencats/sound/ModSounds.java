package net.pevori.queencats.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.pevori.queencats.QueenCats;

public class ModSounds {
    public static SoundEvent HUMANOID_CAT_AMBIENT = registerSoundEvent("humanoid_cat_ambient");
    public static SoundEvent HUMANOID_CAT_EAT = registerSoundEvent("humanoid_cat_eat");
    public static SoundEvent HUMANOID_CAT_HURT = registerSoundEvent("humanoid_cat_hurt");
    public static SoundEvent HUMANOID_CAT_DEATH = registerSoundEvent("humanoid_cat_death");

    public static SoundEvent HUMANOID_DOG_AMBIENT = registerSoundEvent("humanoid_dog_ambient");
    public static SoundEvent HUMANOID_DOG_EAT = registerSoundEvent("humanoid_dog_eat");
    public static SoundEvent HUMANOID_DOG_HURT = registerSoundEvent("humanoid_dog_hurt");
    public static SoundEvent HUMANOID_DOG_ANGRY = registerSoundEvent("humanoid_dog_angry");
    public static SoundEvent HUMANOID_DOG_DEATH = registerSoundEvent("humanoid_dog_death");

    public static SoundEvent HUMANOID_BUNNY_AMBIENT = registerSoundEvent("humanoid_bunny_ambient");
    public static SoundEvent HUMANOID_BUNNY_EAT = registerSoundEvent("humanoid_bunny_eat");
    public static SoundEvent HUMANOID_BUNNY_HURT = registerSoundEvent("humanoid_bunny_hurt");
    public static SoundEvent HUMANOID_BUNNY_DEATH = registerSoundEvent("humanoid_bunny_death");

    public static SoundEvent HUMANOID_COW_AMBIENT = registerSoundEvent("humanoid_cow_ambient");
    public static SoundEvent HUMANOID_COW_EAT = registerSoundEvent("humanoid_cow_eat");
    public static SoundEvent HUMANOID_COW_HURT = registerSoundEvent("humanoid_cow_hurt");
    public static SoundEvent HUMANOID_COW_DEATH = registerSoundEvent("humanoid_cow_death");
    public static SoundEvent HUMANOID_COW_MILK = registerSoundEvent("humanoid_cow_milk");

    public static SoundEvent HUMANOID_ENTITY_SILENT = registerSoundEvent("humanoid_entity_silent");

    private static SoundEvent registerSoundEvent(String name){
        Identifier id = new Identifier(QueenCats.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void bootSounds(){
        
    }
}
