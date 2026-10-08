package fun.modoru.musubi.packet;

import fun.modoru.musubi.peer.MusubiConnection;

@FunctionalInterface
public interface PacketHandler<Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> {

    static <Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> PacketHandler<Instance, Definition> blank() {
        return (_, _, _) -> {};
    }

    void handle(MusubiConnection connection, Definition definition, Instance packet) throws PacketProcessingException;

}
