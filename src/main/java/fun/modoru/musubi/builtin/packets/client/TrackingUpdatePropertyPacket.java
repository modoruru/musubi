package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.UserDefinition;
import fun.modoru.musubi.builtin.data.UserProperty;
import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.builtin.packets.server.PushUserPropertyPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record TrackingUpdatePropertyPacket(Identifier identifier, int propertyId, Object propertyValue) {

    public static final PacketDefinition<TrackingUpdatePropertyPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT, Integer.MIN_VALUE,
            input -> {
                Identifier identifier = Identifier.CODEC.read(input);
                int index = DataUtil.readVarInt(input);
                boolean null0 = input.readBoolean();
                return new TrackingUpdatePropertyPacket(identifier, index, null0 ? null : UserDefinition.byId(index).codec().read(input));
            },
            (output, element) -> {
                Identifier.CODEC.write(output, element.identifier);
                DataUtil.writeVarInt(output, element.propertyId);
                boolean null0 = element.propertyValue == null;
                output.writeBoolean(null0);
                if(!null0) ((UserProperty<Object>) UserDefinition.byId(element.propertyId)).codec().write(output, element.propertyValue);

                return output;
            }
    );

}
