package fun.modoru.musubi;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.util.DataUtil;

public record ProtocolVersion(int[] versions, Flow[] flows) {

    public static final Codec<ProtocolVersion> CODEC = new FixedCodec<>(
            input -> new ProtocolVersion(
                    DataUtil.readVarIntArray(input),
                    DataUtil.readArray(input, Flow[]::new, input1 -> Flow.values()[DataUtil.readVarInt(input1)])
            ),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeVarIntArray,
                    DataUtil.arrayWriter((source, flow) -> DataUtil.writeVarInt(source, flow.ordinal())),
                    element.versions, element.flows
            )
    );

}
