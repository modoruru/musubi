package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record UserTrackingPacket(UUID uuid, boolean track) {

    public static final PacketDefinition<UserTrackingPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new UserTrackingPacket(DataUtil.readUuid(input), input.readBoolean()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ByteBuf::writeBoolean,
                    element.uuid, element.track
            )
    );

}
