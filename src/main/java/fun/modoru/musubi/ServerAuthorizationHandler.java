package fun.modoru.musubi;

import fun.modoru.musubi.server.MusubiProtocolHandler;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ServerAuthorizationHandler {

    boolean authorize(MusubiProtocolHandler handler, @Nullable String authorization);

}
