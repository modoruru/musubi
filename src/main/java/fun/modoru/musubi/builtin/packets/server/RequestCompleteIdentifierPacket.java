package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record RequestCompleteIdentifierPacket(UUID requestUuid, @Nullable UUID uuid, @Nullable UUID gameUuid) {

    public static final PacketDefinition<RequestCompleteIdentifierPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new RequestCompleteIdentifierPacket(DataUtil.readUuid(input), DataUtil.readNullable(input, DataUtil::readUuid), DataUtil.readNullable(input, DataUtil::readUuid)),
            (output, element) -> {
                var uuidWriter = DataUtil.nullableWriter(DataUtil::writeUuid);
                return DataUtil.writeSequentially(
                        output,
                        DataUtil::writeUuid,
                        uuidWriter, uuidWriter,
                        element.requestUuid,
                        element.uuid, element.gameUuid
                );
            }
    );


    public RequestCompleteIdentifierPacket {
        if ((uuid == null) == (gameUuid == null)) throw new IllegalArgumentException("Exactly one parameter should be null.");
    }

}
