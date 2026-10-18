package fun.modoru.musubi.server;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.client.CloseReason;
import fun.modoru.musubi.client.MusubiClient;
import fun.modoru.musubi.client.QuicClientSocket;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.quic.QuicSslContextBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.netty.pkitesting.CertificateBuilder;
import io.netty.pkitesting.X509Bundle;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class ServerTest {

    @SuppressWarnings("deprecation")
    @Test
    public void test() throws Throwable {
        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Protocol serverProtocol = Protocol.server((_, _) -> true).build();
        Protocol clientProtocol = Protocol.client().build();

        X509Bundle ca = new CertificateBuilder()
                .subject("CN=Musubi Test CA")
                .setIsCertificateAuthority(true)
                .setKeyUsage(true,
                        CertificateBuilder.KeyUsage.keyCertSign,
                        CertificateBuilder.KeyUsage.digitalSignature)
                .buildSelfSigned();

        X509Bundle serverCert = new CertificateBuilder()
                .subject("CN=localhost")
                .setKeyUsage(true, CertificateBuilder.KeyUsage.digitalSignature)
                .addExtendedKeyUsage(
                        CertificateBuilder.ExtendedKeyUsage.PKIX_KP_SERVER_AUTH)
                .addSanDnsName("localhost")
                .addSanIpAddress("127.0.0.1")
                .buildIssuedBy(ca);

        MusubiServer server = new MusubiServer(
                serverProtocol,
                new QuicServerSocket(
                        QuicSslContextBuilder.forServer(serverCert.getKeyPair().getPrivate(), null, serverCert.getCertificatePath())
                                .applicationProtocols(QuicServerSocket.PROTOCOL_NAME)
                                .build(),
                        new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), executor),
                        4242
                )
        );

        MusubiClient client = new MusubiClient(
                clientProtocol,
                new QuicClientSocket(
                        QuicSslContextBuilder.forClient()
                                .trustManager(ca.getCertificate())
                                .applicationProtocols(QuicServerSocket.PROTOCOL_NAME)
                                .endpointIdentificationAlgorithm(null)
                                .build(),
                        new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), executor),
                        new InetSocketAddress("127.0.0.1", 4242)
                ),
                null
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

        client.connect();
        client.awaitAuthorization();
        client.close("want to");

        CloseReason closeReason = client.awaitClose();

        server.shutdown();
        switch (closeReason.type()) {
            case UNDEFINED -> System.out.println("client closed unexpectedly");
            case UNAUTHORIZED -> System.out.println("client failed to authorize");
            case WRONG_PROTOCOL -> System.out.println("client and server have different protocols");
            case EXCEPTION -> {
                Assertions.assertNotNull(closeReason.throwable());
                throw closeReason.throwable();
            }
            case LOCAL_CLOSURE -> System.out.println("client closed on its own");
            case GRACEFUL_CLOSURE -> System.out.printf("client closed because of: \"%s\"\n", closeReason.closurePacket().reason());
        }
    }

}
