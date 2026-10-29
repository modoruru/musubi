package fun.modoru.musubi;

import fun.modoru.musubi.builtin.packets.client.*;
import fun.modoru.musubi.builtin.packets.server.*;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketHandler;
import fun.modoru.musubi.server.ServerAuthorizationHandler;
import fun.modoru.musubi.util.Pair;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BuiltInProtocol {

    public static final List<? extends PacketDefinition<?>> DEFINITIONS = List.of(
            PushUserPropertyPacket.DEFINITION,
            UserTrackingStatusPacket.DEFINITION,
            RequestUserDataPacket.DEFINITION,
            RequestCompleteIdentifierPacket.DEFINITION,
            PlayerOnlineStatusPacket.DEFINITION,
            fun.modoru.musubi.builtin.packets.server.BroadcastPlayerMessagePacket.DEFINITION,
            AddUserWarningPacket.DEFINITION,
            RequestUserWarningsPacket.DEFINITION,
            TransferPrivatePlayerMessageRequestPacket.DEFINITION,
            fun.modoru.musubi.builtin.packets.server.TransferPrivatePlayerMessageResponsePacket.DEFINITION,
            DeleteGroupPacket.DEFINITION,
            EditGroupPacket.DEFINITION,
            RequestGroupPacket.DEFINITION,

            UserDataPacket.DEFINITION,
            TrackingUpdatePropertyPacket.DEFINITION,
            CompleteIdentifierPacket.DEFINITION,
            RequestFailedPacket.DEFINITION,
            fun.modoru.musubi.builtin.packets.client.BroadcastPlayerMessagePacket.DEFINITION,
            UserWarningsPacket.DEFINITION,
            TransferPrivatePlayerMessagePacket.DEFINITION,
            fun.modoru.musubi.builtin.packets.client.TransferPrivatePlayerMessageResponsePacket.DEFINITION,
            ConfirmationPacket.DEFINITION,
            GroupPacket.DEFINITION,
            TrackingUpdateGroupPacket.DEFINITION
    );

    private BuiltInProtocol() {}

    public static Builder client() {
        return new Builder(Direction.CLIENT, null);
    }

    public static Builder server(ServerAuthorizationHandler serverAuthorizationHandler) {
        return new Builder(Direction.SERVER, serverAuthorizationHandler);
    }

    public static final class Builder {

        private final Direction acceptingDirection;
        private final @Nullable ServerAuthorizationHandler serverAuthorizationHandler;
        private final Map<PacketDefinition<?>, PacketHandler<?>> definitionsToHandlers;

        private boolean frozen;
        private Protocol result;

        private Builder(Direction acceptingDirection, @Nullable ServerAuthorizationHandler serverAuthorizationHandler) {
            this.acceptingDirection = acceptingDirection;
            this.serverAuthorizationHandler = serverAuthorizationHandler;
            this.definitionsToHandlers = new HashMap<>();
        }

        public <Instance extends Record> Builder defineHandler(PacketDefinition<Instance> definition, PacketHandler<Instance> handler) {
            if(acceptingDirection.opposite() == definition.direction())
                throw new IllegalArgumentException(String.format("You're trying to define handler for packet that has direction \"%s\", but this builder only accepts handlers for \"%s\".", definition.direction().name(), acceptingDirection));
            if(!DEFINITIONS.contains(definition))
                throw new IllegalArgumentException("No such definition in the built-in protocol.");

            definitionsToHandlers.put(definition, handler);
            return this;
        }

        public Protocol build() {
            if(frozen) return result;

            List<Pair<? extends PacketDefinition<?>, PacketHandler<?>>> definitionsAndHandlers = new ArrayList<>();
            PacketHandler<?> blank = PacketHandler.blank();
            for (PacketDefinition<?> definition : DEFINITIONS) {
                PacketHandler<?> packetHandler = definitionsToHandlers.get(definition);
                if(definition.direction() == acceptingDirection) {
                    if(packetHandler == null) throw new IllegalStateException("Some handlers are missing.");
                }
                else packetHandler = blank;

                definitionsAndHandlers.add(new Pair<>(definition, packetHandler));
            }

            frozen = true;
            result = new Protocol(acceptingDirection, serverAuthorizationHandler, definitionsAndHandlers);
            return result;
        }

    }

}
