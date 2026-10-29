package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.packets.RequestPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record CreateGroupRequestPacket(UUID requestUuid, UUID ownerUuid, String name) implements RequestPacket {

    public static final PacketDefinition<CreateGroupRequestPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new CreateGroupRequestPacket(DataUtil.readUuid(input), DataUtil.readUuid(input), DataUtil.readUtf8(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil::writeUuid, DataUtil::writeUtf8,
                    element.requestUuid, element.ownerUuid, element.name
            )
    );

}
