package fun.modoru.musubi.server;

import fun.modoru.musubi.InterruptableRunnable;
import fun.modoru.musubi.Protocol;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.quic.QuicStreamChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class MusubiServer extends ChannelInitializer<QuicStreamChannel> {

    protected final Protocol protocol;
    protected final QuicServerSocket serverSocket;
    protected final Function<InterruptableRunnable, Thread> socketThreadStarter;

    protected final Set<MusubiConnection> connections;

    protected Thread thread;

    public MusubiServer(Protocol protocol, QuicServerSocket serverSocket, Function<InterruptableRunnable, Thread> socketThreadStarter) {
        this.protocol = protocol;
        this.serverSocket = serverSocket;
        this.socketThreadStarter = socketThreadStarter;

        this.connections = new HashSet<>();
    }

    public void start() {
        thread = socketThreadStarter.apply(() -> serverSocket.start(this));
    }

    void addConnection(MusubiConnection connection) {
        connections.add(connection);
    }

    @Override
    protected void initChannel(QuicStreamChannel channel) throws Exception {
        channel.pipeline().addLast(new MusubiProtocolHandler(this));
    }

}
