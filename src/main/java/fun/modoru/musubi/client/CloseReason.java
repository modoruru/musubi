package fun.modoru.musubi.client;

import fun.modoru.musubi.packets.ConnectionClosurePacket;
import org.jspecify.annotations.Nullable;

public final class CloseReason {

    private static final CloseReason
            UNDEFINED = new CloseReason(Type.UNDEFINED, null, null),
            LOCAL_CLOSURE = new CloseReason(Type.LOCAL_CLOSURE, null, null),
            UNAUTHORIZED = new CloseReason(Type.UNAUTHORIZED, null, null),
            WRONG_PROTOCOL = new CloseReason(Type.WRONG_PROTOCOL, null, null);

    private final Type type;
    private final @Nullable Throwable throwable;
    private final @Nullable ConnectionClosurePacket closurePacket;

    private CloseReason(Type type, @Nullable Throwable throwable, @Nullable ConnectionClosurePacket closurePacket) {
        this.type = type;
        this.throwable = throwable;
        this.closurePacket = closurePacket;
    }

    public Type type() {
        return type;
    }

    public @Nullable Throwable throwable() {
        return throwable;
    }

    public @Nullable ConnectionClosurePacket closurePacket() {
        return closurePacket;
    }

    public static CloseReason undefined() {
        return UNDEFINED;
    }

    public static CloseReason exception(Throwable throwable) {
        return new CloseReason(Type.EXCEPTION, throwable, null);
    }

    public static CloseReason gracefulClosure(ConnectionClosurePacket closurePacket) {
        return new CloseReason(Type.GRACEFUL_CLOSURE, null, closurePacket);
    }

    public static CloseReason localClosure() {
        return LOCAL_CLOSURE;
    }

    public static CloseReason unauthorized() {
        return UNAUTHORIZED;
    }

    public static CloseReason wrongProtocol() {
        return WRONG_PROTOCOL;
    }

    public enum Type {
        UNDEFINED,
        EXCEPTION,
        GRACEFUL_CLOSURE,
        LOCAL_CLOSURE,
        UNAUTHORIZED,
        WRONG_PROTOCOL
    }

}
