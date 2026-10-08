package fun.modoru.musubi.packets;

import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record AuthorizationPacket(UUID uuid, @Nullable String authorization) implements PacketInstance {

    public static final PacketDefinition<AuthorizationPacket> DEFINITION = new PacketDefinition<>(
            Flow.SERVER,
            Integer.MIN_VALUE,
            input -> new AuthorizationPacket(
                    DataUtil.readUuid(input),
                    DataUtil.readNullable(input, DataUtil::readUtf8)
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil.nullableWriter(DataUtil::writeUtf8),
                    element.uuid, element.authorization
            )
    );

}
