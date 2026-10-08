package fun.modoru.musubi;

import fun.modoru.musubi.socket.MusubiProtocolHandler;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ServerAuthorizationHandler {

    boolean authorize(MusubiProtocolHandler handler, @Nullable String authorization);

}
