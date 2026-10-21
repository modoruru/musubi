package fun.modoru.musubi.packets;

import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import org.jspecify.annotations.Nullable;

public record ConnectionClosurePacket(@Nullable String reason)  {

    private static final Reader<ConnectionClosurePacket> READER = input -> new ConnectionClosurePacket(DataUtil.readNullable(input, DataUtil::readUtf8));
    private static final Writer<ConnectionClosurePacket> WRITER = (output, element) -> DataUtil.writeNullable(output, DataUtil::writeUtf8, element.reason);

    public static final PacketDefinition<ConnectionClosurePacket>
            CLIENT_DEFINITION = new PacketDefinition<>(Direction.CLIENT, Integer.MIN_VALUE, READER, WRITER),
            SERVER_DEFINITION = new PacketDefinition<>(Direction.SERVER, Integer.MIN_VALUE, READER, WRITER);
}
