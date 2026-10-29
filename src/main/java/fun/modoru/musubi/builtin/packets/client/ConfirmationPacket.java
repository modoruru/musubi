package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.packets.WaitingPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * @see WaitingPacket
 */
public record ConfirmationPacket(UUID waitingUuid, boolean success) {

    public static final PacketDefinition<ConfirmationPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new ConfirmationPacket(DataUtil.readUuid(input), input.readBoolean()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, ByteBuf::writeBoolean,
                    element.waitingUuid, element.success
            )
    );

}
