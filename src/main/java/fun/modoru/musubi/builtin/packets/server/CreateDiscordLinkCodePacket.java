package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.packets.WaitingPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record CreateDiscordLinkCodePacket(UUID waitingUuid, UUID uuid, String code) implements WaitingPacket {

    public static final PacketDefinition<CreateDiscordLinkCodePacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new CreateDiscordLinkCodePacket(DataUtil.readUuid(input), DataUtil.readUuid(input), DataUtil.readUtf8(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil::writeUuid, DataUtil::writeUtf8,
                    element.waitingUuid, element.uuid, element.code
            )
    );

}
