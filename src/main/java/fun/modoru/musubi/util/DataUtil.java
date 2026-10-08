package fun.modoru.musubi.util;

import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

public final class DataUtil {

    private DataUtil() {}

    public static <E1, E2> ByteBuf writeSequentially(ByteBuf output, Writer<E1> e1Writer, Writer<E2> e2Writer, E1 e1, E2 e2) {
        return e2Writer.write(e1Writer.write(output, e1), e2);
    }

    public static <E1, E2, E3> ByteBuf writeSequentially(ByteBuf output, Writer<E1> e1Writer, Writer<E2> e2Writer, Writer<E3> e3Writer, E1 e1, E2 e2, E3 e3) {
        return e3Writer.write(e2Writer.write(e1Writer.write(output, e1), e2), e3);
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

}
