package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record CompleteIdentifierPacket(@Nullable UUID uuid, @Nullable UUID gameUuid) {

    public static final PacketDefinition<CompleteIdentifierPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new CompleteIdentifierPacket( DataUtil.readNullable(input, DataUtil::readUuid), DataUtil.readNullable(input, DataUtil::readUuid)),
            (output, element) -> {
                var uuidWriter = DataUtil.nullableWriter(DataUtil::writeUuid);
                return DataUtil.writeSequentially(
                        output,
                        uuidWriter, uuidWriter,
                        element.uuid, element.gameUuid
                );
            }
    );


    public CompleteIdentifierPacket {
        if ((uuid == null) == (gameUuid == null)) throw new IllegalArgumentException("Exactly one parameter should be null.");
    }

}
