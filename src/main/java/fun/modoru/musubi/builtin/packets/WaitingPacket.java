package fun.modoru.musubi.builtin.packets;

import fun.modoru.musubi.builtin.packets.client.ConfirmationPacket;

import java.util.UUID;

/**
 * Intended for packets which will await a {@link ConfirmationPacket} before proceeding further.
 */
public interface WaitingPacket {

    UUID waitingUuid();

}
