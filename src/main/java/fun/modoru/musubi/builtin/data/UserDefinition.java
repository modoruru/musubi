package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.builtin.data.user.*;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class UserDefinition {

    private static final AtomicInteger GLOBAL_INDEX = new AtomicInteger();
    private static final Map<Integer, UserProperty<?>> ID_TO_PROPERTY = new HashMap<>();

    public static final UserProperty<Identifier> identifier = define(Identifier.CODEC);

    public static final UserProperty<@Nullable Subscription> subscription = defineNullable(Subscription.CODEC);
    public static final UserProperty<@Nullable TeamRole> teamRole = defineNullable(TeamRole.CODEC);

    public static final UserProperty<@Nullable Punishment>
            ban = defineNullable(Punishment.CODEC),
            mute = defineNullable(Punishment.CODEC);

    public static final UserProperty<@Nullable UUID> groupUuid = defineNullable(Codecs.UUID);

    public static final UserProperty<@Nullable AcquiredNameColor> currentNameColor = defineNullable(AcquiredNameColor.CODEC);
    public static final UserProperty<@Nullable Particle> particle = defineNullable(Particle.CODEC);

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

    private static <E> UserProperty<E> defineNullable(Codec<E> nonNullable) {
        return define(new FixedCodec<>(
                input -> DataUtil.readNullable(input, nonNullable),
                (output, element) -> DataUtil.writeNullable(output, nonNullable, element)
        ));
    }

    private static <E> UserProperty<E> define(Codec<E> codec) {
        int index = GLOBAL_INDEX.getAndIncrement();

        UserProperty<E> property = new UserProperty<>(index, codec);
        ID_TO_PROPERTY.put(index, property);
        return property;
    }

}
