package fun.modoru.musubi.packets;

import fun.modoru.musubi.ProtocolVersion;
import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record ProtocolVersionPacket(UUID uuid, ProtocolVersion protocolVersion) implements PacketInstance {

    public static final PacketDefinition<ProtocolVersionPacket> DEFINITION = new PacketDefinition<>(
            Flow.SERVER,
            Integer.MIN_VALUE,
            input -> new ProtocolVersionPacket(
                    DataUtil.readUuid(input),
                    ProtocolVersion.CODEC.read(input)
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ProtocolVersion.CODEC,
                    element.uuid, element.protocolVersion
            )
    );

}
