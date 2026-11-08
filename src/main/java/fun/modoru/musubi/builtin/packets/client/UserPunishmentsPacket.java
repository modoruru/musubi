package fun.modoru.musubi.builtin.packets.client;

import fun.modoru.musubi.builtin.data.user.Punishment;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;

import java.util.Map;
import java.util.UUID;

public record UserPunishmentsPacket(UUID uuid, Map<Punishment.Type, Punishment> punishments) {

    private static final Codec<Map<Punishment.Type, Punishment>> PUNISHMENTS_CODEC = Codecs.mapCodec(Punishment.Type.CODEC, Punishment.CODEC);

    public static final PacketDefinition<UserPunishmentsPacket> DEFINITION = new PacketDefinition<>(
            Direction.CLIENT,
            Integer.MIN_VALUE,
            input -> new UserPunishmentsPacket(DataUtil.readUuid(input), PUNISHMENTS_CODEC.read(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid, PUNISHMENTS_CODEC,
                    element.uuid, element.punishments
            )
    );

}
