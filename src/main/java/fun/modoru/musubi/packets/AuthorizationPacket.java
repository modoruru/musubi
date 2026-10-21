package fun.modoru.musubi.packets;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import org.jspecify.annotations.Nullable;

public record AuthorizationPacket(String identifier, @Nullable String authorization) {

    public static final PacketDefinition<AuthorizationPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new AuthorizationPacket(DataUtil.readUtf8(input), DataUtil.readNullable(input, DataUtil::readUtf8)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUtf8, DataUtil.nullableWriter(DataUtil::writeUtf8),
                    element.identifier, element.authorization
            )
    );

}
