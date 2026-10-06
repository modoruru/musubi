package fun.modoru.musubi.packets;

import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class AuthorizationPacketDefinition implements PacketDefinition<AuthorizationPacketDefinition.AuthorizationPacket> {

    public static final AuthorizationPacketDefinition DEFINITION = new AuthorizationPacketDefinition();

    private AuthorizationPacketDefinition() {}

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
                DataUtil.readNullable(input, DataUtil::readUtf8)
        );
    }

    @Override
    public ByteBuf write(ByteBuf output, AuthorizationPacket element) {
        return DataUtil.writeSequentially(
                output,
                DataUtil::writeUuid, DataUtil.nullableWriter(DataUtil::writeUtf8),
                element.uuid, element.authorization
        );
    }

    public record AuthorizationPacket(UUID uuid, @Nullable String authorization) implements PacketInstance {}

}
