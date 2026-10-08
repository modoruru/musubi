package fun.modoru.musubi.server;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.peer.MusubiConnection;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.quic.QuicStreamChannel;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MusubiServer extends ChannelInitializer<QuicStreamChannel> {

    protected final Protocol protocol;
    protected final QuicServerSocket serverSocket;

    protected final Set<MusubiConnection> connections;

    public MusubiServer(Protocol protocol, QuicServerSocket serverSocket) {
        this.protocol = protocol;
        this.serverSocket = serverSocket;

        this.connections = new HashSet<>();
    }

    /**
     * This method starts server on the current thread.
     * @param startedFuture future to complete when server is started.
     */
    public void start(@Nullable CompletableFuture<@Nullable Void> startedFuture) throws InterruptedException {
        serverSocket.start(this, startedFuture);
    }

    public void shutdown() throws InterruptedException {
        serverSocket.shutdown();
    }

    public boolean running() {
        return serverSocket.running.get();
    }

    void addConnection(MusubiConnection connection) {
        connections.add(connection);
    }

    @Override
    protected void initChannel(QuicStreamChannel channel) throws Exception {
        channel.pipeline().addLast(new MusubiProtocolHandler(this));
    }

}
