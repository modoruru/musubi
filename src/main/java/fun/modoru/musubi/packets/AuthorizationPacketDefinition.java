package fun.modoru.musubi.packets;

import fun.modoru.musubi.ProtocolVersion;
import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public final class AuthorizationPacketDefinition implements PacketDefinition<AuthorizationPacketDefinition.AuthorizationPacket> {

    @Override
    public Flow flow() {
        return Flow.SERVER;
    }

    @Override
    public int version() {
        return Integer.MIN_VALUE;
    }

    @Override
    public AuthorizationPacket read(ByteBuf input) {
        return new AuthorizationPacket(
                DataUtil.readUuid(input),
                ProtocolVersion.CODEC.read(input)
        );
    }

    @Override
    public ByteBuf write(ByteBuf output, AuthorizationPacket element) {
        DataUtil.writeUuid(output, element.uuid);
        ProtocolVersion.CODEC.write(output, element.protocolVersion);
        return output;
    }

    public record AuthorizationPacket(UUID uuid, ProtocolVersion protocolVersion) implements PacketInstance {}

}
