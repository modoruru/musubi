package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.data.Codec;
import org.jspecify.annotations.Nullable;

public final class UserProperty<E> {

    private final int id;
    private final Codec<E> codec;
    private final @Nullable E defaultValue;

    UserProperty(int id, Codec<E> codec, @Nullable E defaultValue) {
        this.id = id;
        this.codec = codec;
        this.defaultValue = defaultValue;
    }

    public int id() {
        return id;
    }

    public Codec<E> codec() {
        return codec;
    }

    public @Nullable E defaultValue() {
        return defaultValue;
    }

}
