package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.data.server.Warning;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record AddUserWarningPacket(UUID uuid, Warning warning) {

    public static final PacketDefinition<AddUserWarningPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new AddUserWarningPacket(DataUtil.readUuid(input), Warning.CODEC.read(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, Warning.CODEC,
                    element.uuid, element.warning
            )
    );

}
