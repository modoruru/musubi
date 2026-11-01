package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

public record Theme(int accentColor, int warningsColor, int errorsColor) {

    public static final Theme DEFAULT = new Theme(0x69a2ff, 0xffd45e, 0xff5555);

    public static final Codec<Theme> CODEC = new FixedCodec<>(
            input -> new Theme(input.readInt(), input.readInt(), input.readInt()),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    ByteBuf::writeInt, ByteBuf::writeInt, ByteBuf::writeInt,
                    element.accentColor, element.warningsColor, element.errorsColor
            )
    );


}
