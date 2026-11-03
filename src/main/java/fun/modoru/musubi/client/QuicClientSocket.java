package fun.modoru.musubi.client;

import fun.modoru.musubi.peer.MusubiFrameDecoder;
import fun.modoru.musubi.peer.MusubiFrameEncoder;
import fun.modoru.musubi.server.QuicServerSocket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.quic.*;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;

public class QuicClientSocket implements AutoCloseable {

    public static final String PROTOCOL_NAME = QuicServerSocket.PROTOCOL_NAME;

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

        ChannelHandler pipeline = new ChannelInitializer<QuicStreamChannel>() {
            @Override
            protected void initChannel(QuicStreamChannel ch) {
                ch.pipeline()
                        .addLast(new MusubiFrameDecoder())
                        .addLast(new MusubiFrameEncoder())
                        .addLast(handler);
            }
        };

        return quicChannel.createStream(QuicStreamType.BIDIRECTIONAL, pipeline).sync().getNow();
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
