package fun.modoru.musubi.peer;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packet.PacketDefinition;
import io.netty.channel.Channel;

public interface MusubiConnection {

    Channel channel();

    Protocol protocol();

    default <I extends Record, D extends PacketDefinition<I>> void send(D definition, I instance) {
        protocol().writeAndFlush(channel(), definition, instance);
    }

    default void close() {
        channel().close();
    }

}
