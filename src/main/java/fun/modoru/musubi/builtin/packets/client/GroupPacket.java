package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.server.Group;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record GroupPacket(UUID requestUuid, Group group) {

    public static final PacketDefinition<GroupPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new GroupPacket(DataUtil.readUuid(input), Group.CODEC.read(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, Group.CODEC,
                    element.requestUuid, element.group
            )
    );

}
