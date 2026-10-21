package fun.modoru.musubi.client;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packets.ConnectionClosurePacket;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MusubiClient implements AutoCloseable {

    protected final Protocol protocol;
    protected final QuicClientSocket quicClientSocket;
    protected final String identifier;
    protected final @Nullable String authorization;
    protected final CompletableFuture<CloseReason> closeReasonFuture;
    protected final CompletableFuture<@Nullable Void> authorizationFuture;

    public @Nullable QuicChannel connection;
    public @Nullable QuicStreamChannel stream;
    public @Nullable ClientConnection clientConnection;

    public MusubiClient(Protocol protocol, QuicClientSocket quicClientSocket, String identifier, @Nullable String authorization) {
        this.protocol = protocol;
        this.quicClientSocket = quicClientSocket;
        this.identifier = identifier;
        this.authorization = authorization;
        this.closeReasonFuture = new CompletableFuture<>();
        this.authorizationFuture = new CompletableFuture<>();
    }

    void setMusubiConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    void finish(CloseReason reason) {
        closeReasonFuture.complete(reason);
    }

    public void connect() throws InterruptedException {
        try {
            connection = quicClientSocket.connect();
            connection.closeFuture().addListener(_ -> finish(CloseReason.undefined()));
            stream = quicClientSocket.openStream(new MusubiProtocolHandler(this));
        }
        catch (Throwable throwable) {
            finish(CloseReason.exception(throwable));
            throw throwable;
        }
    }

    public CloseReason awaitClose() throws InterruptedException, ExecutionException {
        CloseReason closeReason = closeReasonFuture.get();
        if(!authorizationFuture.isDone()) authorizationFuture.complete(null);
        return closeReason;
    }

    public void awaitAuthorization() throws InterruptedException, ExecutionException {
        authorizationFuture.get();
    }

    public boolean connected() {
        return quicClientSocket.isConnected();
    }

    public QuicStreamChannel stream() {
        if (stream == null || !stream.isActive())
            throw new IllegalStateException("musubi client is not connected");

        return stream;
    }

    public void close(@Nullable String reason) {
        finish(CloseReason.localClosure());
        if(clientConnection != null)
            clientConnection.send(ConnectionClosurePacket.SERVER_DEFINITION, new ConnectionClosurePacket(reason));
        quicClientSocket.close();
    }

    @Override
    public void close() {
        close(null);
    }

}
