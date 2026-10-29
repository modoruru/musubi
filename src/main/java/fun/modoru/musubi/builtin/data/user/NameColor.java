package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

public enum NameColor {

    ;

    public static final Codec<NameColor> CODEC = Codecs.enumCodec(NameColor.class);
    private static final Map<String, NameColor> BY_ID = new HashMap<>();

    static {
        for (NameColor value : values()) {
            BY_ID.put(value.id, value);
        }
    }

    private static final int NO_COLOR = -1;

    private final int firstColor, secondColor;
    private final String id;

    NameColor(int firstColor) {
        this(firstColor, NO_COLOR);
    }

    NameColor(int firstColor, int secondColor) {
        this(firstColor, secondColor, null);
    }

    NameColor(int firstColor, int secondColor, @Nullable String idOverride) {
        if(firstColor < 0 || firstColor > 0xffffff) throw new IllegalArgumentException("First color is out of [0; 0xffffff] range.");
        if(secondColor < -1 || secondColor > 0xffffff) throw new IllegalArgumentException("Second color is out of [-1; 0xffffff] range.");
        this.firstColor = firstColor;
        this.secondColor = secondColor;
        this.id = idOverride == null ? name().toLowerCase() : idOverride;
    }

    public int firstColor() {
        return firstColor;
    }

    public OptionalInt secondColor() {
        return secondColor == NO_COLOR ? OptionalInt.empty() : OptionalInt.of(secondColor);
    }

    public String id() {
        return id;
    }

    public static @Nullable NameColor byId(String id) {
        return BY_ID.get(id);
    }

    public record AcquiredNameColor(NameColor nameColor, long receiveTimestamp) {

        public static final Codec<AcquiredNameColor> CODEC = new FixedCodec<>(
                input -> new AcquiredNameColor(NameColor.CODEC.read(input), input.readLong()),
                (output, element) -> DataUtil.writeSequentially(
                        output,
                        NameColor.CODEC, ByteBuf::writeLong,
                        element.nameColor, element.receiveTimestamp
                )
        );

    }

}
