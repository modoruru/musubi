package fun.modoru.musubi.packets;

import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record ConnectionClosurePacket(UUID uuid, @Nullable String reason) implements PacketInstance {

    private static final Reader<ConnectionClosurePacket> READER = input -> new ConnectionClosurePacket(
            DataUtil.readUuid(input),
            DataUtil.readNullable(input, DataUtil::readUtf8)
    );

    private static final Writer<ConnectionClosurePacket> WRITER = (output, element) -> DataUtil.writeSequentially(
            output,
            DataUtil::writeUuid, DataUtil.nullableWriter(DataUtil::writeUtf8),
            element.uuid, element.reason
    );

    public static final PacketDefinition<ConnectionClosurePacket>
            CLIENT_DEFINITION = new PacketDefinition<>(Flow.CLIENT, Integer.MIN_VALUE, READER, WRITER),
            SERVER_DEFINITION = new PacketDefinition<>(Flow.SERVER, Integer.MIN_VALUE, READER, WRITER);
}
