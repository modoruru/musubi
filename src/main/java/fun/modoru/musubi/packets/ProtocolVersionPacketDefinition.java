package fun.modoru.musubi.packets;

import fun.modoru.musubi.ProtocolVersion;
import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public final class ProtocolVersionPacketDefinition implements PacketDefinition<ProtocolVersionPacketDefinition.ProtocolVersionPacket> {

    public static final ProtocolVersionPacketDefinition DEFINITION = new ProtocolVersionPacketDefinition();

    private ProtocolVersionPacketDefinition() {}

    @Override
    public Flow flow() {
        return Flow.SERVER;
    }

    @Override
    public int version() {
        return Integer.MIN_VALUE;
    }

    @Override
    public ProtocolVersionPacket read(ByteBuf input) {
        return new ProtocolVersionPacket(
                DataUtil.readUuid(input),
                ProtocolVersion.CODEC.read(input)
        );
    }

    @Override
    public ByteBuf write(ByteBuf output, ProtocolVersionPacket element) {
        return DataUtil.writeSequentially(
                output,
                DataUtil::writeUuid, ProtocolVersion.CODEC,
                element.uuid, element.protocolVersion
        );
    }

    public record ProtocolVersionPacket(UUID uuid, ProtocolVersion protocolVersion) implements PacketInstance {}

}
