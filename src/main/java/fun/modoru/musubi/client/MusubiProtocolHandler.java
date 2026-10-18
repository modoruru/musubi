package fun.modoru.musubi.client;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packets.*;
import fun.modoru.musubi.peer.MusubiConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class MusubiProtocolHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final MusubiClient musubiClient;

    private @Nullable MusubiConnection connection;
    private AuthorizationState authorizationState = AuthorizationState.START;

    public MusubiProtocolHandler(MusubiClient musubiClient) {
        this.musubiClient = musubiClient;
    }

    void startAuthorization(@Nullable String authorization) {
        connection.send(AuthorizationPacket.DEFINITION, new AuthorizationPacket(UUID.randomUUID(), authorization));
        authorizationState = AuthorizationState.SENT_AUTHORIZATION;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        connection = new MusubiConnection(ctx.channel(), musubiClient.protocol);
        musubiClient.setMusubiConnection(connection);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            startAuthorization(musubiClient.authorization);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Protocol.ReadPacket readPacket = connection.protocol().readPacket(msg);

        // We can accept a connection closure packet regardless of the connection's current state.
        if(readPacket.packetInstance() instanceof ConnectionClosurePacket connectionClosurePacket) {
            musubiClient.finish(CloseReason.gracefulClosure(connectionClosurePacket));
            connection.close();
            return;
        }

        System.out.printf("received packet: %s, authorizatino state: %s\n", readPacket.packetInstance().getClass().getSimpleName(), authorizationState.name());

        switch (authorizationState) {
            case AUTHORIZATION_DECLINE, PROTOCOL_DECLINE -> {
                return;
            }
            case SENT_AUTHORIZATION -> {
                if(readPacket.packetInstance() instanceof AuthorizationResultPacket authorizationResultPacket && authorizationResultPacket.success()) {
                    authorizationState = AuthorizationState.SENT_PROTOCOL;
                    connection.send(ProtocolVersionPacket.DEFINITION, new ProtocolVersionPacket(UUID.randomUUID(), connection.protocol().protocolVersion()));
                    return;
                }

                authorizationState = AuthorizationState.AUTHORIZATION_DECLINE;
                musubiClient.finish(CloseReason.unauthorized());
                connection.close();
            }
            case SENT_PROTOCOL -> {
                if(readPacket.packetInstance() instanceof ProtocolVersionSuccessPacket) {
                    authorizationState = AuthorizationState.DONE;
                    musubiClient.authorizationFuture.complete(null);
                    return;
                }

                authorizationState = AuthorizationState.PROTOCOL_DECLINE;
                musubiClient.finish(CloseReason.unauthorized());
                connection.close();
            }
        }

        connection.protocol().tryToHandle(connection, readPacket);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        musubiClient.finish(CloseReason.undefined());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        musubiClient.finish(CloseReason.exception(cause));
        ctx.close();
    }

    protected enum AuthorizationState {
        START,

        SENT_AUTHORIZATION,
        AUTHORIZATION_DECLINE,

        SENT_PROTOCOL,
        PROTOCOL_DECLINE,

        DONE
    }

}
