package fun.modoru.musubi.packets;

import fun.modoru.musubi.ProtocolVersion;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;

public record ProtocolVersionPacket(ProtocolVersion protocolVersion) {

    public static final PacketDefinition<ProtocolVersionPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new ProtocolVersionPacket(ProtocolVersion.CODEC.read(input)),
            (output, element) -> ProtocolVersion.CODEC.write(output, element.protocolVersion)
    );

}
