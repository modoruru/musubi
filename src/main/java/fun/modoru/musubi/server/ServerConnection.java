package fun.modoru.musubi.server;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.peer.MusubiConnection;
import io.netty.channel.Channel;
import org.jspecify.annotations.Nullable;

public final class ServerConnection implements MusubiConnection {

    public final Channel channel;
    public final Protocol protocol;
    public @Nullable String identifier;

    public ServerConnection(Channel channel, Protocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public Protocol protocol() {
        return protocol;
    }

}
