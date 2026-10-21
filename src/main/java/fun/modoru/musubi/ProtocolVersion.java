package fun.modoru.musubi;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.util.DataUtil;

public record ProtocolVersion(int[] versions, Direction[] directions) {

    public static final Codec<ProtocolVersion> CODEC = new FixedCodec<>(
            input -> new ProtocolVersion(
                    DataUtil.readVarIntArray(input),
                    DataUtil.readArray(input, Direction[]::new, input1 -> Direction.values()[DataUtil.readVarInt(input1)])
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeVarIntArray,
                    DataUtil.arrayWriter((source, direction) -> DataUtil.writeVarInt(source, direction.ordinal())),
                    element.versions, element.directions
            )
    );

}
