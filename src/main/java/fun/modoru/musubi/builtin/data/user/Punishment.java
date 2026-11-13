package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.builtin.data.misc.Length;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public record Punishment(long creationTime, Length length, String reason, UUID moderatorUuid) {

    public static final Codec<Punishment> CODEC = new FixedCodec<>(
            input -> new Punishment(input.readLong(), Length.CODEC.read(input), DataUtil.readUtf8(input), DataUtil.readUuid(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    ByteBuf::writeLong, Length.CODEC,
                    DataUtil::writeUtf8, DataUtil::writeUuid,
                    element.creationTime, element.length,
                    element.reason, element.moderatorUuid
            )
    );

    public boolean active() {
        return length.isInfinite() || System.currentTimeMillis() <= creationTime + length.length();
    }

    public enum Type {
        MUTE, BAN;

        public static final Codec<Type> CODEC = Codecs.enumCodec(Type.class);
    }

}
