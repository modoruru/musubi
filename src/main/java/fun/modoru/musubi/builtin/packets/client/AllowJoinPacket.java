package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.data.Codecs;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record AllowJoinPacket(UUID requestUuid, boolean allow, @Nullable String disallowMessage) {

    public static final PacketDefinition<AllowJoinPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new AllowJoinPacket(DataUtil.readUuid(input), input.readBoolean(), DataUtil.readNullable(input, Codecs.UTF_8)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    Codecs.UUID, ByteBuf::writeBoolean, DataUtil.nullableWriter(Codecs.UTF_8),
                    element.requestUuid, element.allow, element.disallowMessage
            )
    );

}
