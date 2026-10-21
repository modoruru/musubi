package fun.modoru.musubi.builtin.data.misc;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;

public final class Length {

    public static final Codec<Length> CODEC = new FixedCodec<>(
            input -> new Length(input.readLong()),
            (output, element) -> output.writeLong(element.value)
    );

    private static final Length INFINITE = new Length(-1);
    private final long value;

    private Length(long value) {
        this.value = value;
    }

    public boolean isInfinite() {
        return value == -1;
    }

    public boolean isFinite() {
        return value != -1;
    }

    public long length() {
        if(!isFinite()) throw new IllegalStateException();
        return value;
    }

    public static Length infinite() {
        return INFINITE;
    }

    public static Length finite(long value) {
        if(value < 1) throw new IllegalArgumentException("Value should be at least one.");
        return new Length(value);
    }

}
