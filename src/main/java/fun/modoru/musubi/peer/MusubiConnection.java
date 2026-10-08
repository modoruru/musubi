package fun.modoru.musubi.peer;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import io.netty.channel.Channel;

public record MusubiConnection(Channel channel, Protocol protocol) {

    public <I extends Record & PacketInstance, D extends PacketDefinition<I>> void send(D definition, I instance) {
        protocol.writeAndFlush(channel, definition, instance);
    }

    public void close() {
        channel.close();
    }

}
