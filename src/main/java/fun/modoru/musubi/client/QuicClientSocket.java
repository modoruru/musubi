package fun.modoru.musubi.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.quic.*;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class QuicClientSocket implements AutoCloseable {

    protected final QuicSslContext sslContext;
    protected final MultiThreadIoEventLoopGroup eventLoopGroup;
    protected final InetSocketAddress remoteAddress;

    private @Nullable Channel datagramChannel;
    private @Nullable QuicChannel quicChannel;

    public QuicClientSocket(QuicSslContext sslContext, MultiThreadIoEventLoopGroup eventLoopGroup, InetSocketAddress remoteAddress) {
        this.sslContext = sslContext;
        this.eventLoopGroup = eventLoopGroup;
        this.remoteAddress = remoteAddress;
    }

    public QuicChannel connect() throws InterruptedException {
        ChannelHandler codec = new QuicClientCodecBuilder()
                .sslContext(sslContext)
                .maxIdleTimeout(30_000, TimeUnit.MILLISECONDS)
                .initialMaxData(10_000_000)
                .initialMaxStreamDataBidirectionalLocal(1_000_000)
                .initialMaxStreamDataBidirectionalRemote(1_000_000)
                .build();

        datagramChannel = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(codec)
                .bind(0)
                .sync()
                .channel();

        quicChannel = QuicChannel.newBootstrap(datagramChannel)
                .handler(new ChannelInboundHandlerAdapter())
                .remoteAddress(remoteAddress)
                .connect()
                .sync()
                .getNow();

        return quicChannel;
    }

    public QuicStreamChannel openStream(ChannelHandler handler) throws InterruptedException {
        if (quicChannel == null || !quicChannel.isActive()) {
            throw new IllegalStateException("Client is not connected");
        }

        return quicChannel.createStream(QuicStreamType.BIDIRECTIONAL, handler).sync().getNow();
    }

    public boolean isConnected() {
        return quicChannel != null && quicChannel.isActive();
    }

    @Override
    public void close() {
        if (quicChannel != null) {
            quicChannel.close().syncUninterruptibly();
        }
        if (datagramChannel != null) {
            datagramChannel.close().syncUninterruptibly();
        }
    }

}
