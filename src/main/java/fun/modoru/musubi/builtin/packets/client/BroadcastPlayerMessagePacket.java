package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.misc.ChatChannel;
import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

public record BroadcastPlayerMessagePacket(
        Identifier senderIdentifier, Object[] senderPropertiesValues,
        String originalClientName, long timeSent,
        ChatChannel chatChannel, String message
) {

    public static final PacketDefinition<BroadcastPlayerMessagePacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new BroadcastPlayerMessagePacket(
                    Identifier.CODEC.read(input), UserDataPacket.USER_PROPERTIES_VALUES_CODEC.read(input),
                    DataUtil.readUtf8(input), input.readLong(),
                    ChatChannel.CODEC.read(input), DataUtil.readUtf8(input)
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    Identifier.CODEC, UserDataPacket.USER_PROPERTIES_VALUES_CODEC,
                    DataUtil::writeUtf8, ByteBuf::writeLong,
                    ChatChannel.CODEC, DataUtil::writeUtf8,
                    element.senderIdentifier, element.senderPropertiesValues,
                    element.originalClientName, element.timeSent,
                    element.chatChannel, element.message
            )
    );

}
