package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record Identifier(UUID uuid, UUID gameUuid) {

    public static final Codec<Identifier> CODEC = new FixedCodec<>(
            input -> new Identifier(DataUtil.readUuid(input), DataUtil.readUuid(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil::writeUuid,
                    element.uuid, element.gameUuid
            )
    );

}
