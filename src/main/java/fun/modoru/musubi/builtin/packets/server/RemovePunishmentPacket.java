package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.data.user.Punishment;
import fun.modoru.musubi.builtin.packets.WaitingPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record RemovePunishmentPacket(UUID waitingUuid, UUID uuid, Punishment.Type type, UUID moderatorUuid) implements WaitingPacket {

    public static final PacketDefinition<RemovePunishmentPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new RemovePunishmentPacket(DataUtil.readUuid(input), DataUtil.readUuid(input), Punishment.Type.CODEC.read(input), DataUtil.readUuid(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil::writeUuid, Punishment.Type.CODEC, DataUtil::writeUuid,
                    element.waitingUuid, element.uuid, element.type, element.moderatorUuid
            )
    );

}
