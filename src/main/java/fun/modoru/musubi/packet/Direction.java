package fun.modoru.musubi.packet;

import org.jspecify.annotations.Nullable;

public enum Direction {

    UNDEFINED(null),
    CLIENT("SERVER"),
    SERVER("CLIENT");

    private final @Nullable String opposite;
    private @Nullable Direction cachedOpposite;

    Direction(@Nullable String opposite) {
        this.opposite = opposite;
    }

    public @Nullable Direction opposite() {
        if(opposite == null) return null;
        if(cachedOpposite != null) return cachedOpposite;

        return cachedOpposite = valueOf(opposite);
    }

}
