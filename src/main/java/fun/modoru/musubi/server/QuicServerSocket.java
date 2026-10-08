package fun.modoru.musubi.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.quic.*;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuicServerSocket {

    public static final String PROTOCOL_NAME = "musubi";

    protected final QuicSslContext sslContext;
    protected final MultiThreadIoEventLoopGroup group;
    protected final int port;
    public final AtomicBoolean running;

    public Channel channel;
    public MusubiServer musubiServer;

    public QuicServerSocket(File key, File cert, int port) {
        this(
                QuicSslContextBuilder.forServer(key, null, cert)
                        .applicationProtocols(PROTOCOL_NAME)
                        .build(),
                new NioEventLoopGroup(1, (Executor) null),
                port
        );
    }

    public QuicServerSocket(QuicSslContext sslContext, MultiThreadIoEventLoopGroup group, int port) {
        this.sslContext = sslContext;
        this.group = group;
        this.port = port;
        this.running = new AtomicBoolean();
    }

    public void start(MusubiServer musubiServer, @Nullable CompletableFuture<@Nullable Void> startedFuture) throws InterruptedException {
        this.musubiServer = musubiServer;
        try {
            channel = new Bootstrap()
                    .group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(
                            new QuicServerCodecBuilder()
                                    .sslContext(sslContext)
                                    .maxIdleTimeout(30, TimeUnit.SECONDS)
                                    .initialMaxData(10000000)
                                    .initialMaxStreamDataBidirectionalLocal(1000000)
                                    .initialMaxStreamDataBidirectionalRemote(1000000)
                                    .initialMaxStreamsBidirectional(100)
                                    .handler(musubiServer)
                                    .build()
                    )
                    .bind(port)
                    .sync()
                    .channel();
            running.set(true);
            if(startedFuture != null) startedFuture.complete(null);

            channel.closeFuture().sync();
        }
        finally {
            running.set(false);
            group.shutdownGracefully().sync();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.close().sync();
    }

}
