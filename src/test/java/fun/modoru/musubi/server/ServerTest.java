package fun.modoru.musubi.server;

import fun.modoru.musubi.Protocol;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.quic.QuicSslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.junit.jupiter.api.Test;

import java.security.cert.CertificateException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerTest {

    @SuppressWarnings("deprecation")
    @Test
    public void test() throws CertificateException, InterruptedException {
        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Protocol protocol;
        try (Protocol.Builder builder = Protocol.server((connection, authorization) -> true)) {
            protocol = builder.build();
        }

        SelfSignedCertificate cert = new SelfSignedCertificate();

        MusubiServer server = new MusubiServer(
                protocol,
                new QuicServerSocket(
                        QuicSslContextBuilder.forServer(cert.key(), null, cert.cert())
                                .applicationProtocols(QuicServerSocket.PROTOCOL_NAME)
                                .build(),
                        new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), executor),
                        4242
                )
        );

        CompletableFuture<Void> startedFuture = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                server.start(startedFuture);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        startedFuture.join();

        System.out.println("server started!");

        long start = System.currentTimeMillis();
        boolean calledStop = false;
        while (server.running()) {
            Thread.onSpinWait();
            if(!calledStop && System.currentTimeMillis() - start > 5000) {
                calledStop = true;
                server.shutdown();
            }
        }
    }

}
