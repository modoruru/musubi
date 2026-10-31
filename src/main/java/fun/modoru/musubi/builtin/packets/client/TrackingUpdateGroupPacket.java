package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.packets.server.EditGroupPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;

/**
 * Sent only if the client is tracking specific users associated with the update
 */
public record TrackingUpdateGroupPacket(EditGroupPacket.GroupPatch groupPatch) {

    public static final PacketDefinition<TrackingUpdateGroupPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new TrackingUpdateGroupPacket(EditGroupPacket.GroupPatch.CODEC.read(input)),
            (output, element) -> EditGroupPacket.GroupPatch.CODEC.write(output, element.groupPatch)
    );

}
