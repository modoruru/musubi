package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import fun.modoru.musubi.builtin.packets.server.RequestUserDataPacket;
import fun.modoru.musubi.builtin.packets.server.RequestCompleteIdentifierPacket;
import fun.modoru.musubi.builtin.packets.server.RequestUserWarningsPacket;

import java.util.UUID;

/**
 * An answer to {@link RequestUserDataPacket}, {@link RequestUserWarningsPacket} and {@link RequestCompleteIdentifierPacket} packets sent if no data were found.
 * @param requestUuid failed request's uuid
 */
public record RequestFailedPacket(UUID requestUuid) {

    public static final PacketDefinition<RequestFailedPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new RequestFailedPacket(DataUtil.readUuid(input)),
            (output, element) -> DataUtil.writeUuid(output, element.requestUuid)
    );

}
