package fun.modoru.musubi.client;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packets.ConnectionClosurePacket;
import fun.modoru.musubi.peer.MusubiConnection;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MusubiClient implements AutoCloseable {

    protected final Protocol protocol;
    protected final QuicClientSocket quicClientSocket;
    protected final @Nullable String authorization;
    protected final CompletableFuture<CloseReason> closeReasonFuture;
    protected final CompletableFuture<@Nullable Void> authorizationFuture;

    public @Nullable QuicChannel connection;
    public @Nullable QuicStreamChannel stream;
    public @Nullable MusubiConnection musubiConnection;

    public MusubiClient(Protocol protocol, QuicClientSocket quicClientSocket, @Nullable String authorization) {
        this.protocol = protocol;
        this.quicClientSocket = quicClientSocket;
        this.authorization = authorization;
        this.closeReasonFuture = new CompletableFuture<>();
        this.authorizationFuture = new CompletableFuture<>();
    }

    void setMusubiConnection(MusubiConnection musubiConnection) {
        this.musubiConnection = musubiConnection;
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
        if(musubiConnection != null)
            musubiConnection.send(ConnectionClosurePacket.SERVER_DEFINITION, new ConnectionClosurePacket(reason));
        quicClientSocket.close();
    }

    @Override
    public void close() {
        close(null);
    }

}
