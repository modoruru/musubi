package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.AbsurdUtil;
import fun.modoru.musubi.builtin.packets.RequestPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record RequestGroupPacket(UUID requestUuid, UUID groupUuid) implements RequestPacket {

    public static final PacketDefinition<RequestGroupPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new RequestGroupPacket(DataUtil.readUuid(input), DataUtil.readUuid(input)),
            (output, element) -> AbsurdUtil.writeTwoUuids(output, element.requestUuid, element.groupUuid)
    );

}
