package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.data.Codec;
import io.netty.buffer.ByteBuf;

public final class UserProperty<E> implements Codec<E> {

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

    @Override
    public E read(ByteBuf input) {
        return codec.read(input);
    }

    @Override
    public ByteBuf write(ByteBuf output, E element) {
        return codec.write(output, element);
    }

}
