package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record RequestUserDataPacket(UUID uuid) {

    public static final PacketDefinition<RequestUserDataPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new RequestUserDataPacket(DataUtil.readUuid(input)),
            (output, element) -> DataUtil.writeUuid(output, element.uuid)
    );

}
