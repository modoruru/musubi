package fun.modoru.musubi.util;

import fun.modoru.musubi.data.Reader;
import fun.modoru.musubi.data.Writer;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import java.util.function.Function;

public final class DataUtil {

    private DataUtil() {}

    public static UUID readUuid(ByteBuf input) {
        long most = input.readLong(), least = input.readLong();
        return new UUID(most, least);
    }

    public static ByteBuf writeUuid(ByteBuf output, UUID uuid) {
        output.writeLong(uuid.getMostSignificantBits());
        output.writeLong(uuid.getLeastSignificantBits());
        return output;
    }

    public static <Element> Element[] readArray(ByteBuf input, Function<Integer, Element[]> arrayCreator, Reader<Element> elementReader) {
        int size = VarIntUtil.read(input);
        Element[] array = arrayCreator.apply(size);
        for (int i = 0; i < size; i++) {
            array[i] = elementReader.read(input);
        }
        return array;
    }

    public static <Element> ByteBuf writeArray(ByteBuf output, Writer<Element> elementWriter, Element[] array) {
        VarIntUtil.write(output, array.length);
        for (Element element : array) {
            elementWriter.write(output, element);
        }
        return output;
    }

    public static int[] readVarIntArray(ByteBuf input) {
        int size = VarIntUtil.read(input);
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = VarIntUtil.read(input);
        }
        return array;
    }

    public static ByteBuf writeVarIntArray(ByteBuf output, int[] array) {
        VarIntUtil.write(output, array.length);
        for (int element : array) {
            VarIntUtil.write(output, element);
        }
        return output;
    }

}
