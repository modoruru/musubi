package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

public record AcquiredNameColor(String id, long receiptTimestamp) {

    public static final Codec<AcquiredNameColor> CODEC = new FixedCodec<>(
            input -> new AcquiredNameColor(DataUtil.readUtf8(input), input.readLong()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUtf8, ByteBuf::writeLong,
                    element.id, element.receiptTimestamp
            )
    );

}
