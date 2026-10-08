package fun.modoru.musubi.packet;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface PacketHandler<Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> {

    static <Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> PacketHandler<Instance, Definition> blank() {
        return (_, _, _) -> {};
    }

    void handle(ChannelHandlerContext channelHandlerContext, Definition definition, Instance packet) throws PacketProcessingException;

}
