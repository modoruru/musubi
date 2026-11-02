package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.data.user.ChatChannel;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record BroadcastPlayerMessagePacket(UUID senderUuid, long timeSent, ChatChannel chatChannel, String message) {

    public static final PacketDefinition<BroadcastPlayerMessagePacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new BroadcastPlayerMessagePacket(DataUtil.readUuid(input), input.readLong(), ChatChannel.CODEC.read(input), DataUtil.readUtf8(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ByteBuf::writeLong, ChatChannel.CODEC, DataUtil::writeUtf8,
                    element.senderUuid, element.timeSent, element.chatChannel, element.message
            )
    );

}
