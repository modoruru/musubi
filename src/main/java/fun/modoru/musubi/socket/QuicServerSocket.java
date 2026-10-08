package fun.modoru.musubi.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.quic.*;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class QuicServerSocket {

    public static final String PROTOCOL_NAME = "musubi";

    protected final @Nullable QuicSslContext sslContext;
    protected final MultiThreadIoEventLoopGroup group;
    protected final int port;

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

    public QuicServerSocket(@Nullable QuicSslContext sslContext, MultiThreadIoEventLoopGroup group, int port) {
        this.sslContext = sslContext;
        this.group = group;
        this.port = port;
    }

    public void start(MusubiServer musubiServer) throws InterruptedException {
        this.musubiServer = musubiServer;
        try {
            QuicServerCodecBuilder quicServerCodecBuilder = new QuicServerCodecBuilder();
            if(sslContext != null) quicServerCodecBuilder.sslContext(sslContext);

            channel = new Bootstrap()
                    .group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(
                            quicServerCodecBuilder
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

            channel.closeFuture().sync();
        }
        finally {
            group.shutdownGracefully();
        }
    }

}
