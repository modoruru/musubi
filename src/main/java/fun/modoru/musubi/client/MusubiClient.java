package fun.modoru.musubi.client;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packets.ConnectionClosurePacket;
import fun.modoru.musubi.packets.KeepAlivePacket;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.*;

public class MusubiClient implements AutoCloseable {

    public enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        CLOSED
    }

    private static final long KEEP_ALIVE_INTERVAL = TimeUnit.SECONDS.toNanos(5);
    private static final long KEEP_ALIVE_TIMEOUT = TimeUnit.SECONDS.toNanos(10);

    protected final Protocol protocol;
    protected final QuicClientSocket quicClientSocket;
    protected final ScheduledExecutorService scheduledExecutorService;
    protected final String identifier;
    protected final @Nullable String authorization;

    protected volatile State state = State.DISCONNECTED;

    protected volatile @Nullable QuicChannel connection;
    protected volatile @Nullable QuicStreamChannel stream;
    protected volatile @Nullable ClientConnection clientConnection;
    protected volatile @Nullable ScheduledFuture<?> keepAliveTask;

    protected volatile long lastKeepAliveSent;
    protected volatile long pendingKeepAliveSince;
    protected volatile long pendingKeepAliveTimestamp;

    protected volatile CompletableFuture<CloseReason> closeReasonFuture = new CompletableFuture<>();
    protected volatile CompletableFuture<@Nullable Void> authorizationFuture = new CompletableFuture<>();

    public MusubiClient(
            Protocol protocol,
            QuicClientSocket quicClientSocket,
            ScheduledExecutorService scheduledExecutorService,
            String identifier,
            @Nullable String authorization
    ) {
        this.protocol = protocol;
        this.quicClientSocket = quicClientSocket;
        this.scheduledExecutorService = scheduledExecutorService;
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

        lastKeepAliveSent = 0;
        pendingKeepAliveSince = 0;
        pendingKeepAliveTimestamp = 0;

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
        if (!authorizationFuture.complete(null)) {
            return;
        }

        keepAliveTask = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!connected()) return;

                long now = System.nanoTime();
                if (pendingKeepAliveSince != 0) {
                    if (now - pendingKeepAliveSince >= KEEP_ALIVE_TIMEOUT)
                        close("KeepAlive timeout");
                    return;
                }

                if (lastKeepAliveSent != 0 && now - lastKeepAliveSent < KEEP_ALIVE_INTERVAL) return;

                long timestamp = System.currentTimeMillis();

                pendingKeepAliveTimestamp = timestamp;
                pendingKeepAliveSince = now;
                lastKeepAliveSent = now;

                ClientConnection current = clientConnection;
                if (current == null) {
                    close("Client connection is unavailable");
                    return;
                }

                current.send(KeepAlivePacket.SERVER_DEFINITION, new KeepAlivePacket(timestamp));
            }
            catch (Throwable throwable) {
                finish(CloseReason.exception(throwable));
                quicClientSocket.close();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    void finish(CloseReason reason) {
        if (!closeReasonFuture.complete(reason)) {
            return;
        }

        state = State.CLOSED;
        ScheduledFuture<?> task = keepAliveTask;
        if (task != null) {
            task.cancel(false);
            keepAliveTask = null;
        }

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

        if(keepAliveTask != null)
            keepAliveTask.cancel(true);

        quicClientSocket.close();
    }

    @Override
    public void close() {
        close(null);
    }

}