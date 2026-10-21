package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.UserDefinition;
import fun.modoru.musubi.builtin.data.UserProperty;
import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

public record UserDataPacket(Identifier identifier, int[] indices, Object[] elements) {

    public static final PacketDefinition<UserDataPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT, Integer.MIN_VALUE,
            input -> {
                Identifier identifier = Identifier.CODEC.read(input);

                int[] indices = DataUtil.readVarIntArray(input);
                Object[] elements = new Object[indices.length];
                for (int i = 0; i < elements.length; i++) {
                    elements[i] = UserDefinition.byId(indices[i]).codec().read(input);
                }

                return new UserDataPacket(identifier, indices, elements);
            },
            (output, element) -> {
                Identifier.CODEC.write(output, element.identifier);
                DataUtil.writeVarIntArray(output, element.indices);
                for (int i = 0; i < element.elements.length; i++) {
                    ((UserProperty<Object>) UserDefinition.byId(element.indices[i])).codec().write(output, element.elements[i]);
                }
                return output;
            }
    );

}
