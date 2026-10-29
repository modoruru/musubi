package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.AbsurdUtil;
import fun.modoru.musubi.builtin.packets.WaitingPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record DeleteGroupPacket(UUID waitingUuid, UUID groupUuid) implements WaitingPacket {

    public static final PacketDefinition<DeleteGroupPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new DeleteGroupPacket(DataUtil.readUuid(input), DataUtil.readUuid(input)),
            (output, element) -> AbsurdUtil.writeTwoUuids(output, element.waitingUuid, element.groupUuid)
    );

}
