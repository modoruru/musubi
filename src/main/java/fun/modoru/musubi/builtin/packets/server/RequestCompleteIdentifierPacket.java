package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.packets.RequestPacket;
import fun.modoru.musubi.data.Codecs;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record RequestCompleteIdentifierPacket(UUID requestUuid, @Nullable UUID uuid, @Nullable UUID gameUuid, @Nullable String gameName, @Nullable Long discordId) implements RequestPacket {

    public static final PacketDefinition<RequestCompleteIdentifierPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new RequestCompleteIdentifierPacket(
                    DataUtil.readUuid(input),
                    DataUtil.readNullable(input, DataUtil::readUuid),
                    DataUtil.readNullable(input, DataUtil::readUuid),
                    DataUtil.readNullable(input, DataUtil::readUtf8),
                    DataUtil.readNullable(input, ByteBuf::readLong)
            ),
            (output, element) -> {
                var uuidWriter = DataUtil.nullableWriter(DataUtil::writeUuid);
                return DataUtil.writeSequentially(
                        output,
                        Codecs.UUID, uuidWriter, uuidWriter,
                        DataUtil.nullableWriter(DataUtil::writeUtf8), DataUtil.nullableWriter(ByteBuf::writeLong),
                        element.requestUuid, element.uuid, element.gameUuid,
                        element.gameName, element.discordId
                );
            }
    );


    public RequestCompleteIdentifierPacket {
        if ((uuid == null) == (gameUuid == null)) throw new IllegalArgumentException("Exactly one parameter should be null.");
    }

}
