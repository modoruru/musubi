package fun.modoru.musubi.peer;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.packet.PacketDefinition;
import io.netty.channel.Channel;

public record MusubiConnection(Channel channel, Protocol protocol) {

    public <I extends Record, D extends PacketDefinition<I>> void send(D definition, I instance) {
        protocol.writeAndFlush(channel, definition, instance);
    }

    public void close() {
        channel.close();
    }

}
