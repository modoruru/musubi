package fun.modoru.musubi.server;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.ProtocolVersion;
import fun.modoru.musubi.packets.*;
import fun.modoru.musubi.peer.MusubiConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

public class MusubiProtocolHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final MusubiServer musubiServer;

    private MusubiConnection connection;
    private boolean authorizationState;
    private boolean agreedOnProtocol;

    public MusubiProtocolHandler(MusubiServer musubiServer) {
        this.musubiServer = musubiServer;
    }

    private void close(String reason) {
        connection.send(ConnectionClosurePacket.CLIENT_DEFINITION, new ConnectionClosurePacket(UUID.randomUUID(), reason));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        connection = new MusubiConnection(ctx.channel(), musubiServer.protocol);
        musubiServer.addConnection(connection);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        Protocol.ReadPacket readPacket = connection.protocol().readPacket(msg);

        // We can accept a connection closure packet regardless of the connection's current state.
        if(readPacket.packetInstance() instanceof ConnectionClosurePacket connectionClosurePacket) {
            connection.close();
            return;
        }

        // Client's authorized and sent us its protocol so we can transfer packet to packet's handler safely
        if(authorizationState && agreedOnProtocol) {
            try {
                connection.protocol().tryToHandle(connection, readPacket);
            }
            catch (Throwable throwable) {
                connection.close();
                throw throwable;
            }
            return;
        }

        // Client is either not authorized or haven't sent us its protocol version
        if(readPacket.packetInstance() instanceof AuthorizationPacket authorizationPacket) {
            authorizationState = connection.protocol().serverAuthorizationHandler().authorize(connection, authorizationPacket.authorization());
            connection.send(AuthorizationResultPacket.DEFINITION, new AuthorizationResultPacket(UUID.randomUUID(), authorizationState));
            return;
        }

        // If packet is not an authorization one and client's not authorized, close connection with an error.
        if(!authorizationState) {
            close("Authorize first.");
            return;
        }

        if(readPacket.packetInstance() instanceof ProtocolVersionPacket protocolVersionPacket) {
            ProtocolVersion serverProtocol = connection.protocol().protocolVersion();
            ProtocolVersion clientProtocol = protocolVersionPacket.protocolVersion();
            int serverPacketsAmount = serverProtocol.versions().length, clientPacketsAmount = clientProtocol.versions().length;
            if(serverPacketsAmount != clientPacketsAmount) {
                close("Protocols are different - can't continue.");
                return;
            }

            for (int i = 0; i < serverPacketsAmount; i++) {
                if(serverProtocol.versions()[i] != clientProtocol.versions()[i] || serverProtocol.flows()[i] != clientProtocol.flows()[i]) {
                    close("Protocols are different - can't continue.");
                    return;
                }
            }

            agreedOnProtocol = true;
            connection.send(ProtocolVersionSuccessPacket.DEFINITION, new ProtocolVersionSuccessPacket(UUID.randomUUID()));
            return;
        }

        close("Sent your protocol version first.");
    }

}
