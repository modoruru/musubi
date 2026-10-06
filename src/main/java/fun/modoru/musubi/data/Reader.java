package fun.modoru.musubi.data;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface Reader<Readable> {

    Readable read(ByteBuf input);

}
