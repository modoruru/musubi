package fun.modoru.musubi.builtin;

import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * Utils that shouldn't exist, but actually save time.
 */
public final class AbsurdUtil {

    private AbsurdUtil() {}

    public static ByteBuf writeTwoUuids(ByteBuf output, UUID first, UUID second) {
        return DataUtil.writeSequentially(output, DataUtil::writeUuid, DataUtil::writeUuid, first, second);
    }

}
