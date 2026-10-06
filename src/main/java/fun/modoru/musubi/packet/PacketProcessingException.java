package fun.modoru.musubi.packet;

public class PacketProcessingException extends RuntimeException {

    public PacketProcessingException(String message) {
        super(message);
    }

    public PacketProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PacketProcessingException(Throwable throwable) {
        super(throwable);
    }

}
