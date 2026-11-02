package fun.modoru.musubi.client;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packets.ConnectionClosurePacket;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MusubiClient implements AutoCloseable {

    public enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        CLOSED
    }

    protected final Protocol protocol;
    protected final QuicClientSocket quicClientSocket;
    protected final String identifier;
    protected final @Nullable String authorization;

    protected volatile State state = State.DISCONNECTED;

    protected volatile @Nullable QuicChannel connection;
    protected volatile @Nullable QuicStreamChannel stream;
    protected volatile @Nullable ClientConnection clientConnection;

    protected volatile CompletableFuture<CloseReason> closeReasonFuture = new CompletableFuture<>();
    protected volatile CompletableFuture<@Nullable Void> authorizationFuture = new CompletableFuture<>();

    public MusubiClient(
            Protocol protocol,
            QuicClientSocket quicClientSocket,
            String identifier,
            @Nullable String authorization
    ) {
        this.protocol = protocol;
        this.quicClientSocket = quicClientSocket;
        this.identifier = identifier;
        this.authorization = authorization;
    }

    public synchronized void connect() throws InterruptedException {
        if (state == State.CONNECTING || state == State.CONNECTED) {
            return;
        }

        closeReasonFuture = new CompletableFuture<>();
        authorizationFuture = new CompletableFuture<>();

        connection = null;
        stream = null;
        clientConnection = null;

        state = State.CONNECTING;

        try {
            QuicChannel quicChannel = quicClientSocket.connect();
            connection = quicChannel;
            quicChannel.closeFuture().addListener(_ -> finish(CloseReason.undefined()));

            QuicStreamChannel streamChannel = quicClientSocket.openStream(new MusubiProtocolHandler(this));
            stream = streamChannel;
            streamChannel.closeFuture().addListener(_ -> finish(CloseReason.undefined()));

            if (state == State.CONNECTING) {
                state = State.CONNECTED;
            }
        }
        catch (InterruptedException | RuntimeException | Error exception) {
            finish(CloseReason.exception(exception));
            throw exception;
        }
    }

    void setMusubiConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    void authorizationSucceeded() {
        authorizationFuture.complete(null);
    }

    void finish(CloseReason reason) {
        if (!closeReasonFuture.complete(reason)) return;

        state = State.CLOSED;
        authorizationFuture.completeExceptionally(new IllegalStateException("Connection closed before authorization"));
    }

    public CloseReason awaitClose() throws InterruptedException, ExecutionException {
        return closeReasonFuture.get();
    }

    public void awaitAuthorization() throws InterruptedException, ExecutionException {
        authorizationFuture.get();
    }

    public boolean connected() {
        QuicChannel current = connection;
        QuicStreamChannel currentStream = stream;

        return state == State.CONNECTED
                && current != null
                && current.isActive()
                && currentStream != null
                && currentStream.isActive();
    }

    public State state() {
        return state;
    }

    public QuicStreamChannel stream() {
        QuicStreamChannel current = stream;

        if (!connected() || current == null)
            throw new IllegalStateException("musubi client is not connected");

        return current;
    }

    public @Nullable ClientConnection clientConnection() {
        return clientConnection;
    }

    public void close(@Nullable String reason) {
        if (state == State.DISCONNECTED || state == State.CLOSED) {
            return;
        }

        finish(CloseReason.localClosure());

        ClientConnection current = clientConnection;
        if (current != null)
            current.send(ConnectionClosurePacket.SERVER_DEFINITION, new ConnectionClosurePacket(reason));

        quicClientSocket.close();
    }

    @Override
    public void close() {
        close(null);
    }
}