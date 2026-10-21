package fun.modoru.musubi.server;

import fun.modoru.musubi.Protocol;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MusubiServer {

    protected final Protocol protocol;
    protected final QuicServerSocket serverSocket;

    protected final Set<ServerConnection> connections;
    protected final Map<String, ServerConnection> identifiedConnections;

    public MusubiServer(Protocol protocol, QuicServerSocket serverSocket) {
        this.protocol = protocol;
        this.serverSocket = serverSocket;

        this.connections = new HashSet<>();
        this.identifiedConnections = new HashMap<>();
    }

    /**
     * This method starts server on the current thread.
     * @param startedFuture future to complete when server is started.
     */
    public void start(@Nullable CompletableFuture<@Nullable Void> startedFuture) throws InterruptedException {
        serverSocket.start(this, startedFuture);
    }

    public void shutdown() throws InterruptedException {
        serverSocket.shutdown();
    }

    public boolean running() {
        return serverSocket.running.get();
    }

    void identify(String identifier, ServerConnection connection) {
        identifiedConnections.put(identifier, connection);
    }

    void addConnection(ServerConnection connection) {
        connections.add(connection);
    }

    protected void onClientDisconnected(ServerConnection connection) {
        connections.remove(connection);
        if(connection.identifier != null) identifiedConnections.remove(connection.identifier);
    }

}
