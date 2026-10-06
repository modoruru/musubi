package fun.modoru.musubi.data;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface Writer<Writable> {

    ByteBuf write(ByteBuf output, Writable element);

}
