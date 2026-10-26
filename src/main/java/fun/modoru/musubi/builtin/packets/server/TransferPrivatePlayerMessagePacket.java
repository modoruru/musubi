package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record TransferPrivatePlayerMessagePacket(UUID requestUuid, long creationTime, UUID senderUuid, String message, String receiverGameName) {

    public static final PacketDefinition<TransferPrivatePlayerMessagePacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new TransferPrivatePlayerMessagePacket(DataUtil.readUuid(input), input.readLong(), DataUtil.readUuid(input), DataUtil.readUtf8(input), DataUtil.readUtf8(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ByteBuf::writeLong,
                    DataUtil::writeUuid, DataUtil::writeUtf8, DataUtil::writeUtf8,
                    element.requestUuid, element.creationTime,
                    element.senderUuid, element.message, element.receiverGameName
            )
    );

}
