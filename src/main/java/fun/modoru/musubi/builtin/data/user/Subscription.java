package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.builtin.data.misc.Length;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

public final class Subscription {

    public static final Codec<Subscription> CODEC = new FixedCodec<>(
            input -> new Subscription(input.readLong(), Length.CODEC.read(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    ByteBuf::writeLong, Length.CODEC,
                    element.whenObtained, element.length
            )
    );

    private final long whenObtained;
    private final Length length;

    private Subscription(long whenObtained, Length length) {
        this.whenObtained = whenObtained;
        this.length = length;
    }

    public static Subscription createInfinite() {
        return new Subscription(System.currentTimeMillis(), Length.infinite());
    }

    public static Subscription createFinite(long length) {
        if(length <= 0) length = 1;
        return new Subscription(System.currentTimeMillis(), Length.finite(length));
    }

    public long leftTime() {
        if(!active() || infinite()) return -1;
        return (whenObtained + length.length()) - System.currentTimeMillis();
    }

    public boolean infinite() {
        return length.isInfinite();
    }

    public boolean active() {
        return length.isInfinite() || System.currentTimeMillis() <= whenObtained + length.length();
    }

}
