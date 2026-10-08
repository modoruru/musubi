package fun.modoru.musubi;

import fun.modoru.musubi.peer.MusubiConnection;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ServerAuthorizationHandler {

    boolean authorize(MusubiConnection connection, @Nullable String authorization);

}
