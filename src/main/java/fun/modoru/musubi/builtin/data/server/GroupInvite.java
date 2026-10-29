package fun.modoru.musubi.builtin.data.server;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record GroupInvite(UUID groupUuid, UUID inviteUuid, long expirationTime) {

    public static final Codec<GroupInvite> CODEC = new FixedCodec<>(
            input -> new GroupInvite(DataUtil.readUuid(input), DataUtil.readUuid(input), input.readLong()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil::writeUuid, ByteBuf::writeLong,
                    element.groupUuid, element.inviteUuid, element.expirationTime
            )
    );

}
