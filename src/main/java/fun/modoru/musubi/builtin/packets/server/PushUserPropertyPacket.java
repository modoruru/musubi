package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.data.UserDefinition;
import fun.modoru.musubi.builtin.data.UserProperty;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record PushUserPropertyPacket(UUID uuid, int propertyId, Object propertyValue) {

    public static final PacketDefinition<PushUserPropertyPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> {
                UUID uuid = DataUtil.readUuid(input);
                int index = DataUtil.readVarInt(input);
                boolean null0 = input.readBoolean();
                return new PushUserPropertyPacket(uuid, index, null0 ? null : UserDefinition.byId(index).codec().read(input));
            },
            (output, element) -> {
                DataUtil.writeUuid(output, element.uuid);
                DataUtil.writeVarInt(output, element.propertyId);
                boolean null0 = element.propertyValue == null;
                output.writeBoolean(null0);
                if(!null0) ((UserProperty<Object>) UserDefinition.byId(element.propertyId)).codec().write(output, element.propertyValue);

                return output;
            }
    );

}
