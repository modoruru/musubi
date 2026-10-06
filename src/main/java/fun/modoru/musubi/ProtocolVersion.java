package fun.modoru.musubi;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.util.DataUtil;
import fun.modoru.musubi.util.VarIntUtil;

public record ProtocolVersion(int[] versions, Flow[] flows) {

    public static final Codec<ProtocolVersion> CODEC = new FixedCodec<>(
            input -> new ProtocolVersion(
                    DataUtil.readVarIntArray(input),
                    DataUtil.readArray(input, Flow[]::new, input1 -> Flow.values()[VarIntUtil.read(input1)])
            ),
            (output, element) -> {
                DataUtil.writeVarIntArray(output, element.versions);
                DataUtil.writeArray(output, (output1, element1) -> VarIntUtil.write(output1, element1.ordinal()), element.flows());
                return output;
            }
    );

}
