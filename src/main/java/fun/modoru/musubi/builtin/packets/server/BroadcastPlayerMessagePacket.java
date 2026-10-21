package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record BroadcastPlayerMessagePacket(String chatChannel, UUID senderUuid, String message, long creationTime) {

    public static final PacketDefinition<BroadcastPlayerMessagePacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new BroadcastPlayerMessagePacket(DataUtil.readUtf8(input), DataUtil.readUuid(input), DataUtil.readUtf8(input), input.readLong()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUtf8, DataUtil::writeUuid, DataUtil::writeUtf8, ByteBuf::writeLong,
                    element.chatChannel,
                    element.senderUuid,
                    element.message,
                    element.creationTime
            )
    );

}
