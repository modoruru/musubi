package fun.modoru.musubi.data;

import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public final class Codecs {

    public static final Codec<UUID> UUID = new FixedCodec<>(DataUtil::readUuid, DataUtil::writeUuid);

    private Codecs() {}

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> clazz) {
        return new FixedCodec<>(input -> DataUtil.readEnum(input, clazz), DataUtil::writeEnum);
    }

}
