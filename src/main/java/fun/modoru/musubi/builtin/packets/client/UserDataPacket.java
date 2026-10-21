package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.UserDefinition;
import fun.modoru.musubi.builtin.data.UserProperty;
import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

public record UserDataPacket(Identifier identifier, Object[] propertiesValues) {

    public static final Codec<Object[]> USER_PROPERTIES_VALUES_CODEC = new FixedCodec<>(
            input -> {
                Object[] elements = new Object[DataUtil.readVarInt(input)];
                for (int i = 0; i < elements.length; i++) {
                    elements[i] = UserDefinition.byId(i).codec().read(input);
                }
                return elements;
            },
            (output, elements) -> {
                DataUtil.writeVarInt(output, elements.length);
                for (int i = 0; i < elements.length; i++) {
                    ((UserProperty<Object>) UserDefinition.byId(i)).codec().write(output, elements[i]);
                }
                return output;
            }
    );

    public static final PacketDefinition<UserDataPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT, Integer.MIN_VALUE,
            input -> new UserDataPacket(Identifier.CODEC.read(input), USER_PROPERTIES_VALUES_CODEC.read(input)),
            (output, element) -> {
                Identifier.CODEC.write(output, element.identifier);
                return USER_PROPERTIES_VALUES_CODEC.write(output, element.propertiesValues);
            }
    );

}
