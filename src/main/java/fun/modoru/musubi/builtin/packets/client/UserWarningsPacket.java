package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.server.Warning;
import fun.modoru.musubi.builtin.data.user.Identifier;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.List;

public record UserWarningsPacket(Identifier identifier, List<Warning> warnings) {

    public static final PacketDefinition<UserWarningsPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT, Integer.MIN_VALUE,
            input -> new UserWarningsPacket(Identifier.CODEC.read(input), DataUtil.readList(input, Warning.CODEC)),
            (output, element) -> {
                Identifier.CODEC.write(output, element.identifier);
                return DataUtil.writeCollection(output, Warning.CODEC, element.warnings);
            }
    );

}
