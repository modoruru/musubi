package fun.modoru.musubi.builtin.data.server;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public record Warning(long creationTime, Severity severity, Source source, String reason, @Nullable String messageContent, @Nullable UUID moderatorUuid) {

    public static final Codec<Warning> CODEC = new FixedCodec<>(
            input -> new Warning(
                    input.readLong(),
                    DataUtil.readEnum(input, Severity.class),
                    DataUtil.readEnum(input, Source.class),
                    DataUtil.readUtf8(input),
                    DataUtil.readNullable(input, DataUtil::readUtf8),
                    DataUtil.readNullable(input, DataUtil::readUuid)
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    ByteBuf::writeLong,
                    DataUtil.enumWriter(),
                    DataUtil.enumWriter(),
                    DataUtil::writeUtf8,
                    DataUtil.nullableWriter(DataUtil::writeUtf8),
                    DataUtil.nullableWriter(DataUtil::writeUuid),
                    element.creationTime, element.severity, element.source, element.reason, element.messageContent, element.moderatorUuid
            )
    );

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum Source {
        UNDEFINED,
        DISCORD,
        GAME
    }

}
