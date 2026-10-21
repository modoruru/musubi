package fun.modoru.musubi.packets;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;

public record ProtocolVersionSuccessPacket() {

    public static PacketDefinition<ProtocolVersionSuccessPacket> DEFINITION = PacketDefinition.emptyPacket(Direction.CLIENT, Integer.MIN_VALUE, ProtocolVersionSuccessPacket::new);

}
