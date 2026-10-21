package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record PlayerOnlineStatusPacket(UUID uuid, boolean online) {

    public static final PacketDefinition<PlayerOnlineStatusPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new PlayerOnlineStatusPacket(DataUtil.readUuid(input), input.readBoolean()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ByteBuf::writeBoolean,
                    element.uuid, element.online
            )
    );

}
