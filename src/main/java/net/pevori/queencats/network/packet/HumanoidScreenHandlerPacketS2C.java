package net.pevori.queencats.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.pevori.queencats.QueenCats;

public record HumanoidScreenHandlerPacketS2C(int entityId) implements CustomPayload {
    public static final Identifier HUMANOID_ANIMAL_ENTITY_ID =
            Identifier.of(QueenCats.MOD_ID, "humanoid_animal_entity_id");
    public static final CustomPayload.Id<HumanoidScreenHandlerPacketS2C> ID = new CustomPayload.Id<>(HUMANOID_ANIMAL_ENTITY_ID);
    public static final PacketCodec<RegistryByteBuf, HumanoidScreenHandlerPacketS2C> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, HumanoidScreenHandlerPacketS2C::entityId,
            HumanoidScreenHandlerPacketS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
