package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.UserDefinition;
import fun.modoru.musubi.builtin.data.UserProperty;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record ClientBoundUserPropertyPacket<E>(UUID uuid, int index, E element) implements PacketInstance {

    public static final PacketDefinition<ClientBoundUserPropertyPacket<?>> DEFINITION = new PacketDefinition<>(
            Flow.CLIENT, Integer.MIN_VALUE,
            input -> {
                UUID uuid = DataUtil.readUuid(input);
                int index = DataUtil.readVarInt(input);
                return new ClientBoundUserPropertyPacket<>(uuid, index, UserDefinition.byId(index).read(input));
            },
            (output, element) -> {
                DataUtil.writeUuid(output, element.uuid);
                DataUtil.writeVarInt(output, element.index);
                ((UserProperty<Object>) UserDefinition.byId(element.index)).write(output, element.element);
                return output;
            }
    );

}
