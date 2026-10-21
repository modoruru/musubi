package fun.modoru.musubi.server;

import fun.modoru.musubi.peer.MusubiConnection;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ServerAuthorizationHandler {

    boolean authorize(MusubiConnection connection, String identifier, @Nullable String authorization);

}
