package fun.modoru.musubi.util;

import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class DataUtil {

    public static final Writer<Enum<?>> ENUM_WRITER = (output, element) -> output.writeInt(element.ordinal());

    private DataUtil() {}

    public static <E1, E2> ByteBuf writeSequentially(
            ByteBuf output,
            Writer<E1> e1Writer, Writer<E2> e2Writer,
            E1 e1, E2 e2
    ) {
        return e2Writer.write(e1Writer.write(output, e1), e2);
    }

    public static <E1, E2, E3> ByteBuf writeSequentially(
            ByteBuf output,
            Writer<E1> e1Writer, Writer<E2> e2Writer, Writer<E3> e3Writer,
            E1 e1, E2 e2, E3 e3
    ) {
        return e3Writer.write(e2Writer.write(e1Writer.write(output, e1), e2), e3);
    }

    public static <E1, E2, E3, E4> ByteBuf writeSequentially(
            ByteBuf output,
            Writer<E1> e1Writer, Writer<E2> e2Writer, Writer<E3> e3Writer, Writer<E4> e4Writer,
            E1 e1, E2 e2, E3 e3, E4 e4
    ) {
        return e4Writer.write(e3Writer.write(e2Writer.write(e1Writer.write(output, e1), e2), e3), e4);
    }

    public static <E1, E2, E3, E4, E5> ByteBuf writeSequentially(
            ByteBuf output,
            Writer<E1> e1Writer, Writer<E2> e2Writer, Writer<E3> e3Writer, Writer<E4> e4Writer, Writer<E5> e5Writer,
            E1 e1, E2 e2, E3 e3, E4 e4, E5 e5
    ) {
        return e5Writer.write(e4Writer.write(e3Writer.write(e2Writer.write(e1Writer.write(output, e1), e2), e3), e4), e5);
    }

    public static <E1, E2, E3, E4, E5, E6> ByteBuf writeSequentially(
            ByteBuf output,
            Writer<E1> e1Writer, Writer<E2> e2Writer, Writer<E3> e3Writer, Writer<E4> e4Writer, Writer<E5> e5Writer, Writer<E6> e6Writer,
            E1 e1, E2 e2, E3 e3, E4 e4, E5 e5, E6 e6
    ) {
        return e6Writer.write(e5Writer.write(e4Writer.write(e3Writer.write(e2Writer.write(e1Writer.write(output, e1), e2), e3), e4), e5), e6);
    }

    public static <E1, E2, E3, E4, E5, E6, E7> ByteBuf writeSequentially(
            ByteBuf output,
            Writer<E1> e1Writer, Writer<E2> e2Writer, Writer<E3> e3Writer, Writer<E4> e4Writer, Writer<E5> e5Writer, Writer<E6> e6Writer, Writer<E7> e7Writer,
            E1 e1, E2 e2, E3 e3, E4 e4, E5 e5, E6 e6, E7 e7
    ) {
        return e7Writer.write(e6Writer.write(e5Writer.write(e4Writer.write(e3Writer.write(e2Writer.write(e1Writer.write(output, e1), e2), e3), e4), e5), e6), e7);
    }

    public static boolean hasContinuationBit(byte in) {
        return (in & 128) == 128;
    }

    public static int readVarInt(ByteBuf input) {
        int out = 0;
        int bytes = 0;

        byte in;
        do {
            in = input.readByte();
            out |= (in & 127) << (bytes++ * 7);
            if (bytes > 5) {
                throw new RuntimeException("VarInt too big");
            }
        }
        while(hasContinuationBit(in));

        return out;
    }

    public static ByteBuf writeVarInt(ByteBuf output, int value) {
        if ((value & -128) == 0)
            return output.writeByte(value);

        if ((value & -16384) == 0) {
            int s = (value & 127 | 128) << 8 | value >>> 7;
            return output.writeShort(s);
        }

        return writeVarIntSlow(output, value);
    }

    public static ByteBuf writeVarIntSlow(ByteBuf output, int value) {
        while((value & -128) != 0) {
            output.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        return output.writeByte(value);
    }

    public static UUID readUuid(ByteBuf input) {
        long most = input.readLong(), least = input.readLong();
        return new UUID(most, least);
    }

    public static ByteBuf writeUuid(ByteBuf output, UUID uuid) {
        return output.writeLong(uuid.getMostSignificantBits()).writeLong(uuid.getLeastSignificantBits());
    }

    public static <Element> Element[] readArray(ByteBuf input, Function<Integer, Element[]> arrayCreator, Reader<Element> elementReader) {
        int size = readVarInt(input);
        Element[] array = arrayCreator.apply(size);
        for (int i = 0; i < size; i++) {
            array[i] = elementReader.read(input);
        }
        return array;
    }

    public static <Element> ByteBuf writeArray(ByteBuf output, Writer<Element> elementWriter, Element[] array) {
        writeVarInt(output, array.length);
        for (Element element : array) {
            output = elementWriter.write(output, element);
        }
        return output;
    }

    public static <Element> Writer<Element[]> arrayWriter(Writer<Element> elementWriter) {
        return (output, element) -> writeArray(output, elementWriter, element);
    }

    public static int[] readVarIntArray(ByteBuf input) {
        int size = readVarInt(input);
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = readVarInt(input);
        }
        return array;
    }

    public static ByteBuf writeVarIntArray(ByteBuf output, int[] array) {
        writeVarInt(output, array.length);
        for (int element : array) {
            writeVarInt(output, element);
        }
        return output;
    }

    public static <Element> @Nullable Element readNullable(ByteBuf input, Reader<Element> elementReader) {
        if(!input.readBoolean()) return null;
        return elementReader.read(input);
    }

    public static <Element> ByteBuf writeNullable(ByteBuf output, Writer<Element> elementWriter, @Nullable Element element) {
        output = output.writeBoolean(element != null);
        if(element != null) output = elementWriter.write(output, element);

        return output;
    }

    public static <Element> Writer<Element> nullableWriter(Writer<Element> nonNullWriter) {
        return (output, element) -> writeNullable(output, nonNullWriter, element);
    }

    public static String readString(ByteBuf input, Charset charset) {
        int size = input.readInt();
        byte[] raw = new byte[size];
        input.readBytes(raw);
        return new String(raw, charset);
    }

    public static ByteBuf writeString(ByteBuf output, Charset charset, String string) {
        byte[] raw = string.getBytes(charset);
        return output.writeInt(raw.length).writeBytes(raw);
    }

    public static String readUtf8(ByteBuf input) {
        return readString(input, StandardCharsets.UTF_8);
    }

    public static ByteBuf writeUtf8(ByteBuf output, String utf8) {
        return writeString(output, StandardCharsets.UTF_8, utf8);
    }

    public static <Element extends Enum<Element>> Element readEnum(ByteBuf input, Class<Element> clazz) {
        return clazz.getEnumConstants()[input.readInt()];
    }

    public static <Element extends Enum<Element>> ByteBuf writeEnum(ByteBuf output, Element element) {
        return output.writeInt(element.ordinal());
    }

    public static <Element extends Enum<Element>> Writer<Element> enumWriter() {
        return (Writer<Element>) ENUM_WRITER;
    }

    public static <Element, AbstractCollection extends Collection<Element>> AbstractCollection readCollection(
            ByteBuf input,
            Function<Integer, AbstractCollection> collectionConstructor,
            BiConsumer<AbstractCollection, Element> addFunction,
            Reader<Element> elementReader
    ) {
        int size = readVarInt(input);
        AbstractCollection collection = collectionConstructor.apply(size);
        for (int i = 0; i < size; i++) {
            addFunction.accept(collection, elementReader.read(input));
        }
        return collection;
    }

    public static <Element> ByteBuf writeCollection(ByteBuf output, Writer<Element> elementWriter, Collection<Element> collection) {
        writeVarInt(output, collection.size());
        for (Element element : collection) {
            output = elementWriter.write(output, element);
        }
        return output;
    }

    public static <Element> List<Element> readList(ByteBuf input, Reader<Element> elementReader) {
        return readCollection(input, ArrayList::new, List::add, elementReader);
    }

    public static <Element> Set<Element> readSet(ByteBuf input, Reader<Element> elementReader) {
        return readCollection(input, HashSet::new, Set::add, elementReader);
    }

}
