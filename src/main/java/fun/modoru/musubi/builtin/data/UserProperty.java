package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.data.Codec;

public final class UserProperty<E> {

    private final int index;
    private final Codec<E> codec;

    UserProperty(int index, Codec<E> codec) {
        this.index = index;
        this.codec = codec;
    }

    public int index() {
        return index;
    }

    public Codec<E> codec() {
        return codec;
    }

}
