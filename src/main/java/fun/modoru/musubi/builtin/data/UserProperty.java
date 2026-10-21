package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.data.Codec;

public final class UserProperty<E> {

    private final int id;
    private final Codec<E> codec;

    UserProperty(int id, Codec<E> codec) {
        this.id = id;
        this.codec = codec;
    }

    public int id() {
        return id;
    }

    public Codec<E> codec() {
        return codec;
    }

}
