package fun.modoru.musubi.packet;

import org.jspecify.annotations.Nullable;

/**
 * Where to
 */
public enum Flow {

    UNDEFINED(null),
    CLIENT("server"),
    SERVER("client");

    private final @Nullable String oppositeFlow;

    Flow(@Nullable String oppositeFlow) {
        this.oppositeFlow = oppositeFlow;
    }

    public @Nullable Flow oppositeFlow() {
        if(oppositeFlow == null) return null;
        return valueOf(oppositeFlow);
    }

}
