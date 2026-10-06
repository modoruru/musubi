package fun.modoru.musubi.packet;

@FunctionalInterface
public interface PacketHandler<Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> {

    void handle(Definition definition, Instance packet) throws PacketProcessingException;

}
