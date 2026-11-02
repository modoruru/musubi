package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record TransferPrivatePlayerMessagePacket(
        UUID requestUuid,
        long creationTime,
        String senderClient,

        Identifier senderIdentifier,
        Object[] senderPropertiesValues,

        Identifier receiverIdentifier,

        String message
) {

    public static final PacketDefinition<TransferPrivatePlayerMessagePacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new TransferPrivatePlayerMessagePacket(
                    DataUtil.readUuid(input),
                    input.readLong(),
                    DataUtil.readUtf8(input),

                    Identifier.CODEC.read(input),
                    UserDataPacket.USER_PROPERTIES_VALUES_CODEC.read(input),

                    Identifier.CODEC.read(input),

                    DataUtil.readUtf8(input)
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ByteBuf::writeLong, DataUtil::writeUtf8,
                    Identifier.CODEC, UserDataPacket.USER_PROPERTIES_VALUES_CODEC,
                    Identifier.CODEC,
                    DataUtil::writeUtf8,
                    element.requestUuid, element.creationTime, element.senderClient,
                    element.senderIdentifier, element.senderPropertiesValues,
                    element.receiverIdentifier,
                    element.message
            )
    );

}
