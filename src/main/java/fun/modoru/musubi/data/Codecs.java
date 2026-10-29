package fun.modoru.musubi.data;

import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.*;

public final class Codecs {

    // Primitives
    public static final Codec<Byte> BYTE = new FixedCodec<>(ByteBuf::readByte, (output, element) -> output.writeByte(element));
    public static final Codec<Integer> INT = new FixedCodec<>(ByteBuf::readInt, ByteBuf::writeInt);
    public static final Codec<Long> LONG = new FixedCodec<>(ByteBuf::readLong, ByteBuf::writeLong);
    public static final Codec<Float> FLOAT = new FixedCodec<>(ByteBuf::readFloat, ByteBuf::writeFloat);
    public static final Codec<Double> DOUBLE = new FixedCodec<>(ByteBuf::readDouble, ByteBuf::writeDouble);
    public static final Codec<Boolean> BOOLEAN = new FixedCodec<>(ByteBuf::readBoolean, ByteBuf::writeBoolean);

    // Complex values
    public static final Codec<String> UTF_8 = new FixedCodec<>(DataUtil::readUtf8, DataUtil::writeUtf8);
    public static final Codec<UUID> UUID = new FixedCodec<>(DataUtil::readUuid, DataUtil::writeUuid);
    public static final Codec<Integer> VAR_INT = new FixedCodec<>(DataUtil::readVarInt, DataUtil::writeVarInt);

    private Codecs() {}

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> clazz) {
        return new FixedCodec<>(input -> DataUtil.readEnum(input, clazz), DataUtil::writeEnum);
    }

    public static <E> Codec<List<E>> listCodec(Codec<E> elementCodec) {
        return new FixedCodec<>(
                input -> DataUtil.readCollection(input, ArrayList::new, List::add, elementCodec),
                (output, element) -> DataUtil.writeCollection(output, elementCodec, element)
        );
    }

    public static <E> Codec<Set<E>> setCodec(Codec<E> elementCodec) {
        return new FixedCodec<>(
                input -> DataUtil.readCollection(input, HashSet::new, Set::add, elementCodec),
                (output, element) -> DataUtil.writeCollection(output, elementCodec, element)
        );
    }

}
