package fun.modoru.musubi.builtin.data.server;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.Set;
import java.util.UUID;

public record Group(UUID groupUuid, UUID ownerUuid, String name, Set<UUID> members, int iconColor) {

    public static final Codec<Group> CODEC = new FixedCodec<>(
            input -> new Group(
                    DataUtil.readUuid(input),
                    DataUtil.readUuid(input),
                    DataUtil.readUtf8(input),
                    DataUtil.readSet(input, DataUtil::readUuid),
                    input.readInt()
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,

                    DataUtil::writeUuid, DataUtil::writeUuid, DataUtil::writeUtf8,
                    DataUtil.collectionWriter(DataUtil::writeUuid),
                    ByteBuf::writeInt,

                    element.groupUuid, element.ownerUuid, element.name,
                    element.members,
                    element.iconColor
            )
    );

}
