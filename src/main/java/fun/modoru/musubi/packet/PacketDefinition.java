package fun.modoru.musubi.packet;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import java.util.function.Function;

public record PacketDefinition<Instance extends Record & PacketInstance>(Flow flow, int version, Reader<Instance> reader, Writer<Instance> writer) implements Codec<Instance> {

    public static <Instance extends Record & PacketInstance> PacketDefinition<Instance> onlyUuidPacket(Flow flow, int version, Function<UUID, Instance> constructor) {
        return new PacketDefinition<>(
                flow,
                version,
                input -> constructor.apply(DataUtil.readUuid(input)),
                (output, element) -> DataUtil.writeUuid(output, element.uuid())
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
