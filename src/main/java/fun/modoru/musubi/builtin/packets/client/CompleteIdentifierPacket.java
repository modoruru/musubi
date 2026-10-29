package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.AbsurdUtil;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record CompleteIdentifierPacket(UUID uuid, UUID gameUuid) {

    public static final PacketDefinition<CompleteIdentifierPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new CompleteIdentifierPacket(DataUtil.readUuid(input), DataUtil.readUuid(input)),
            (output, element) -> AbsurdUtil.writeTwoUuids(output, element.uuid, element.gameUuid)
    );

}
