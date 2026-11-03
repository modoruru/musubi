package fun.modoru.musubi.packets;

import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;

public record KeepAlivePacket(long timestamp) {

    private static final Reader<KeepAlivePacket> READER = input -> new KeepAlivePacket(input.readLong());
    private static final Writer<KeepAlivePacket> WRITER = (output, element) -> output.writeLong(element.timestamp);

    public static PacketDefinition<KeepAlivePacket>
            CLIENT_DEFINITION = new PacketDefinition<>(Direction.CLIENT, Integer.MIN_VALUE, READER, WRITER),
            SERVER_DEFINITION = new PacketDefinition<>(Direction.SERVER, Integer.MIN_VALUE, READER, WRITER);

}
