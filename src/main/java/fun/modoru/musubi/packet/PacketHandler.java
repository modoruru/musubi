package fun.modoru.musubi.packet;

import fun.modoru.musubi.peer.MusubiConnection;

@FunctionalInterface
public interface PacketHandler<Instance extends Record> {

    static <Instance extends Record> PacketHandler<Instance> blank() {
        return (_, _, _) -> {};
    }

    void handle(MusubiConnection connection, PacketDefinition<Instance> definition, Instance packet) throws PacketProcessingException;

}
