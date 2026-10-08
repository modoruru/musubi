package fun.modoru.musubi.packets;

import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record AuthorizationResultPacket(UUID uuid, boolean success) implements PacketInstance {

    public static final PacketDefinition<AuthorizationResultPacket> DEFINITION = new PacketDefinition<>(
            Flow.CLIENT, Integer.MIN_VALUE,
            input -> new AuthorizationResultPacket(DataUtil.readUuid(input), input.readBoolean()),
            (output, element) -> DataUtil.writeSequentially(
                    output, DataUtil::writeUuid, ByteBuf::writeBoolean,
                    element.uuid, element.success
            )
    );

}
