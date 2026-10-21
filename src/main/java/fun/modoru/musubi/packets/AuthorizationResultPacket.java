package fun.modoru.musubi.packets;

import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;

public record AuthorizationResultPacket(boolean success) {

    public static final PacketDefinition<AuthorizationResultPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT, Integer.MIN_VALUE,
            input -> new AuthorizationResultPacket(input.readBoolean()),
            (output, element) -> output.writeBoolean(element.success)
    );

}
