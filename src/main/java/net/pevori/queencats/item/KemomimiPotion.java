package net.pevori.queencats.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.pevori.queencats.QueenCats;
import net.pevori.queencats.entity.ModEntities;
import net.pevori.queencats.entity.custom.*;
import net.pevori.queencats.entity.variant.*;

public class KemomimiPotion extends GlintedItem{
    public KemomimiPotion(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if(!player.getWorld().isClient()) {
            if(entity instanceof CatEntity catEntity && entity.isAlive()){
                HumanoidCatEntity humanoidCatEntity = catEntity.isBaby()
                        ? ModEntities.PRINCESS_CAT.create(catEntity.getWorld())
                        : ModEntities.QUEEN_CAT.create(catEntity.getWorld());

                assert humanoidCatEntity != null;
                var animalVariant = Util.getRandom(HumanoidCatVariant.values(), player.getRandom());
                spawnHumanoidAnimal(humanoidCatEntity, catEntity, player, animalVariant);
            }
            else if(entity instanceof WolfEntity wolfEntity && entity.isAlive()){
                HumanoidDogEntity humanoidDogEntity = wolfEntity.isBaby()
                        ? ModEntities.PRINCESS_DOG.create(wolfEntity.getWorld())
                        : ModEntities.QUEEN_DOG.create(wolfEntity.getWorld());

                assert humanoidDogEntity != null;
                var animalVariant = Util.getRandom(HumanoidDogVariant.values(), player.getRandom());
                spawnHumanoidAnimal(humanoidDogEntity, wolfEntity, player, animalVariant);
            }
            else if(entity instanceof RabbitEntity rabbitEntity && entity.isAlive()){
                HumanoidBunnyEntity humanoidBunnyEntity = rabbitEntity.isBaby()
                        ? ModEntities.PRINCESS_BUNNY.create(rabbitEntity.getWorld())
                        : ModEntities.QUEEN_BUNNY.create(rabbitEntity.getWorld());

                assert humanoidBunnyEntity != null;
                var animalVariant = Util.getRandom(HumanoidBunnyVariant.values(), player.getRandom());
                spawnHumanoidAnimal(humanoidBunnyEntity, rabbitEntity, player, animalVariant);
            }
            else if(entity instanceof CowEntity cowEntity && entity.isAlive()){
                HumanoidCowEntity humanoidCowEntity = cowEntity.isBaby()
                        ? ModEntities.PRINCESS_COW.create(cowEntity.getWorld())
                        : ModEntities.QUEEN_COW.create(cowEntity.getWorld());

                assert humanoidCowEntity != null;
                var animalVariant = Util.getRandom(HumanoidCowVariant.values(), player.getRandom());
                spawnHumanoidAnimal(humanoidCowEntity, cowEntity, player, animalVariant);
            }
            else if(entity instanceof SheepEntity sheepEntity && entity.isAlive()){
                HumanoidSheepEntity humanoidSheepEntity = sheepEntity.isBaby()
                        ? ModEntities.QUEEN_SHEEP.create(sheepEntity.getWorld())
                        : ModEntities.QUEEN_SHEEP.create(sheepEntity.getWorld());

                assert humanoidSheepEntity != null;
                var animalVariant = Util.getRandom(HumanoidCowVariant.values(), player.getRandom());
                humanoidSheepEntity.setColor(sheepEntity.getColor());
                spawnHumanoidAnimal(humanoidSheepEntity, sheepEntity, player, animalVariant);
            }
        }

        return ActionResult.SUCCESS;
    }

    public void spawnHumanoidAnimal(HumanoidAnimalEntity humanoidAnimalEntity, AnimalEntity animalEntity, PlayerEntity player, HumanoidAnimalVariant variant){
        humanoidAnimalEntity.refreshPositionAndAngles(animalEntity.getX(), animalEntity.getY(), animalEntity.getZ(), animalEntity.getYaw(), animalEntity.getPitch());
        humanoidAnimalEntity.setAiDisabled(animalEntity.isAiDisabled());

        if (animalEntity.hasCustomName()) {
            humanoidAnimalEntity.setCustomName(animalEntity.getCustomName());
            humanoidAnimalEntity.setCustomNameVisible(animalEntity.isCustomNameVisible());
        }

        humanoidAnimalEntity.setPersistent();
        humanoidAnimalEntity.setOwnerUuid(player.getUuid());
        humanoidAnimalEntity.setTamed(true);
        humanoidAnimalEntity.setSit(true);

        humanoidAnimalEntity.setVariant(variant);

        animalEntity.getWorld().spawnEntity(humanoidAnimalEntity);
        animalEntity.discard();
    }
}
