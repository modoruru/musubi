package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.data.user.Punishment;
import fun.modoru.musubi.builtin.packets.WaitingPacket;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.UUID;

public record AddPunishmentPacket(UUID waitingUuid, UUID uuid, Punishment.Type type, Punishment punishment) implements WaitingPacket {

    public static final PacketDefinition<AddPunishmentPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new AddPunishmentPacket(DataUtil.readUuid(input), DataUtil.readUuid(input), Punishment.Type.CODEC.read(input), Punishment.CODEC.read(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, DataUtil::writeUuid, Punishment.Type.CODEC, Punishment.CODEC,
                    element.waitingUuid, element.uuid, element.type, element.punishment
            )
    );
    
}
