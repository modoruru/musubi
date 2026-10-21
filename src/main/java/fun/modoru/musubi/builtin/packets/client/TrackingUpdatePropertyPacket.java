package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.UserDefinition;
import fun.modoru.musubi.builtin.data.UserProperty;
import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

public record TrackingUpdatePropertyPacket(Identifier identifier, int index, Object element) {

    public static final PacketDefinition<TrackingUpdatePropertyPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT, Integer.MIN_VALUE,
            input -> {
                Identifier identifier = Identifier.CODEC.read(input);
                int index = DataUtil.readVarInt(input);
                return new TrackingUpdatePropertyPacket(identifier, index, UserDefinition.byId(index).codec().read(input));
            },
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    Identifier.CODEC, DataUtil::writeVarInt, ((UserProperty<Object>) UserDefinition.byId(element.index)).codec(),
                    element.identifier, element.index, element.element
            )
    );

}
