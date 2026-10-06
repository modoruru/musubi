package fun.modoru.musubi.data;

import io.netty.buffer.ByteBuf;

public record FixedCodec<Element>(Reader<Element> reader, Writer<Element> writer) implements Codec<Element> {

    @Override
    public Element read(ByteBuf input) {
        return reader.read(input);
    }

    @Override
    public ByteBuf write(ByteBuf output, Element element) {
        return writer.write(output, element);
    }

}
