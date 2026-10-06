package fun.modoru.musubi.packet;

import fun.modoru.musubi.data.Codec;
import io.netty.buffer.ByteBuf;

public interface PacketDefinition<Instance extends Record & PacketInstance> extends Codec<Instance> {

    Flow flow();

    int version();

    Instance read(ByteBuf input);

    ByteBuf write(ByteBuf output, Instance element);

}
