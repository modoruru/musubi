package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.builtin.data.user.*;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class UserDefinition {

    private static final AtomicInteger GLOBAL_INDEX = new AtomicInteger();
    private static final Map<Integer, UserProperty<?>> ID_TO_PROPERTY = new HashMap<>();

    public static final Codec<Long> LONG_CODEC = new FixedCodec<>(ByteBuf::readLong, ByteBuf::writeLong);

    public static final UserProperty<Long>
            creationDate = define(LONG_CODEC),
            discordId = define(LONG_CODEC);

    public static final UserProperty<String> lastKnownUsername = define(new FixedCodec<>(DataUtil::readUtf8, DataUtil::writeUtf8));

    public static final UserProperty<Subscription> subscription = define(Subscription.CODEC);
    public static final UserProperty<TeamRole> teamRole = define(TeamRole.CODEC);

    public static final UserProperty<Punishment>
            ban = define(Punishment.CODEC),
            mute = define(Punishment.CODEC);

    public static final UserProperty<UUID> groupUuid = define(Codecs.UUID);

    public static final UserProperty<AcquiredNameColor> currentNameColor = define(AcquiredNameColor.CODEC);
    public static final UserProperty<Particle> particle = define(Particle.CODEC);

    private UserDefinition() {}

    public static UserProperty<?> byId(int id) {
        return ID_TO_PROPERTY.get(id);
    }

    public static int length() {
        return GLOBAL_INDEX.get();
    }

    public static UserProperty<?>[] properties() {
        UserProperty<?>[] result = new UserProperty[length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ID_TO_PROPERTY.get(i);
        }
        return result;
    }

    private static <E> UserProperty<E> define(Codec<E> codec) {
        int index = GLOBAL_INDEX.getAndIncrement();

        UserProperty<E> property = new UserProperty<>(index, codec);
        ID_TO_PROPERTY.put(index, property);
        return property;
    }

}
