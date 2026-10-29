package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.packets.WaitingPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record UserTrackingStatusPacket(UUID waitingUuid, UUID uuid, boolean track) implements WaitingPacket {

    public static final PacketDefinition<UserTrackingStatusPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new UserTrackingStatusPacket(DataUtil.readUuid(input), DataUtil.readUuid(input), input.readBoolean()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil::writeUuid, ByteBuf::writeBoolean,
                    element.waitingUuid, element.uuid, element.track
            )
    );

}
