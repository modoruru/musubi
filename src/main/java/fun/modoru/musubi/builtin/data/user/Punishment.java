package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.builtin.data.misc.Length;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record Punishment(long creationTime, Length length, @Nullable String reason, @Nullable UUID moderatorUuid) {

    public static final Codec<Punishment> CODEC = new FixedCodec<>(
            input -> new Punishment(input.readLong(), Length.CODEC.read(input), DataUtil.readNullable(input, DataUtil::readUtf8), DataUtil.readNullable(input, DataUtil::readUuid)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    ByteBuf::writeLong, Length.CODEC,
                    DataUtil.nullableWriter(DataUtil::writeUtf8), DataUtil.nullableWriter(DataUtil::writeUuid),
                    element.creationTime, element.length,
                    element.reason, element.moderatorUuid
            )
    );

    public boolean active() {
        return length.isInfinite() || System.currentTimeMillis() <= creationTime + length.length();
    }

}
