package fun.modoru.musubi.packets;

import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;

import java.util.UUID;

public record ProtocolVersionSuccessPacket(UUID uuid) implements PacketInstance {

    public static PacketDefinition<ProtocolVersionSuccessPacket> DEFINITION = PacketDefinition.onlyUuidPacket(Flow.CLIENT, Integer.MIN_VALUE, ProtocolVersionSuccessPacket::new);

}
