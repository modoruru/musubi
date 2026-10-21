package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

public record BroadcastPlayerMessagePacket(
        String originalClient, String chatChannel,
        Identifier senderIdentifier, Object[] senderPropertiesValues,
        String message, long creationTime
) {

    public static final PacketDefinition<BroadcastPlayerMessagePacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new BroadcastPlayerMessagePacket(
                    DataUtil.readUtf8(input), DataUtil.readUtf8(input),
                    Identifier.CODEC.read(input), UserDataPacket.USER_PROPERTIES_VALUES_CODEC.read(input),
                    DataUtil.readUtf8(input), input.readLong()
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUtf8, DataUtil::writeUtf8,
                    Identifier.CODEC, UserDataPacket.USER_PROPERTIES_VALUES_CODEC,
                    DataUtil::writeUtf8, ByteBuf::writeLong,
                    element.originalClient, element.chatChannel,
                    element.senderIdentifier, element.senderPropertiesValues,
                    element.message, element.creationTime
            )
    );

}
