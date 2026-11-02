package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record TransferPrivatePlayerMessageResponsePacket(UUID requestUuid, boolean success, @Nullable Identifier receiverIdentifier, Object @Nullable [] receiverPropertiesValues) {

    public static final PacketDefinition<TransferPrivatePlayerMessageResponsePacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new TransferPrivatePlayerMessageResponsePacket(
                    DataUtil.readUuid(input),
                    input.readBoolean(),
                    DataUtil.readNullable(input, Identifier.CODEC),
                    DataUtil.readNullable(input, UserDataPacket.USER_PROPERTIES_VALUES_CODEC)
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ByteBuf::writeBoolean,
                    DataUtil.nullableWriter(Identifier.CODEC), DataUtil.nullableWriter(UserDataPacket.USER_PROPERTIES_VALUES_CODEC),
                    element.requestUuid, element.success,
                    element.receiverIdentifier, element.receiverPropertiesValues
            )
    );

}
