package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.packets.RequestPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

/**
 * A response to packets that implement {@link RequestPacket}; sent in the event that the data was not found.
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
