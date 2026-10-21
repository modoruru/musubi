package fun.modoru.musubi.packet;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import io.netty.buffer.ByteBuf;

import java.util.function.Supplier;

public record PacketDefinition<Instance extends Record>(Direction direction, int version, Reader<Instance> reader, Writer<Instance> writer) implements Codec<Instance> {

    public static <Instance extends Record> PacketDefinition<Instance> emptyPacket(Direction direction, int version, Supplier<Instance> constructor) {
        return new PacketDefinition<>(
                direction,
                version,
                _ -> constructor.get(),
                (output, _) -> output
        );
    }

    @Override
    public Instance read(ByteBuf input) {
        return reader.read(input);
    }

    @Override
    public ByteBuf write(ByteBuf output, Instance element) {
        return writer.write(output, element);
    }

}
