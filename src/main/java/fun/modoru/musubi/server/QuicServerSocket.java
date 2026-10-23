package fun.modoru.musubi.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.quic.*;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuicServerSocket {

    public static final String PROTOCOL_NAME = "musubi";

    protected final QuicSslContext sslContext;
    protected final MultiThreadIoEventLoopGroup eventLoopGroup;
    protected final int port;
    public final AtomicBoolean running;

    public volatile Channel channel;
    public volatile MusubiServer musubiServer;

    public QuicServerSocket(QuicSslContext sslContext, MultiThreadIoEventLoopGroup eventLoopGroup, int port) {
        this.sslContext = sslContext;
        this.eventLoopGroup = eventLoopGroup;
        this.port = port;
        this.running = new AtomicBoolean();
    }

    public void start(MusubiServer musubiServer, @Nullable CompletableFuture<@Nullable Void> startedFuture) throws InterruptedException {
        this.musubiServer = musubiServer;
        try {
            channel = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioDatagramChannel.class)
                    .handler(
                            new QuicServerCodecBuilder()
                                    .sslContext(sslContext)
                                    .maxIdleTimeout(30, TimeUnit.SECONDS)
                                    .initialMaxData(10000000)
                                    .initialMaxStreamDataBidirectionalLocal(1000000)
                                    .initialMaxStreamDataBidirectionalRemote(1000000)
                                    .initialMaxStreamsBidirectional(100)
                                    .streamHandler(new ChannelInitializer<QuicStreamChannel>() {
                                        @Override
                                        protected void initChannel(QuicStreamChannel ch) {
                                            ch.pipeline().addLast(new MusubiProtocolHandler(musubiServer));
                                        }
                                    })
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
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.close().sync();
    }

}
