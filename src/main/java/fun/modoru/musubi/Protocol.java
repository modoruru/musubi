package fun.modoru.musubi;

import fun.modoru.musubi.packet.*;
import fun.modoru.musubi.packets.*;
import fun.modoru.musubi.peer.MusubiConnection;
import fun.modoru.musubi.server.ServerAuthorizationHandler;
import fun.modoru.musubi.util.DataUtil;
import fun.modoru.musubi.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class Protocol {

    public static final int RESERVED_PACKETS = 10;

    private final Direction direction;
    private final @Nullable ServerAuthorizationHandler serverAuthorizationHandler;
    private final ProtocolVersion protocolVersion;
    private final PacketDefinition<?>[] definitions;
    private final PacketHandler<?>[] handlers;

    Protocol(Direction direction, @Nullable ServerAuthorizationHandler serverAuthorizationHandler, List<Pair<? extends PacketDefinition<?>, PacketHandler<?>>> definitionsAndHandlers) {
        this.direction = direction;
        this.serverAuthorizationHandler = serverAuthorizationHandler;

        int size = definitionsAndHandlers.size() + RESERVED_PACKETS;
        protocolVersion = new ProtocolVersion(
                new int[size],
                new Direction[size]
        );
        definitions = new PacketDefinition[size];
        handlers = new PacketHandler[size];

        includeReservations(
                protocolVersion,
                definitions,
                handlers,
                ConnectionClosurePacket.CLIENT_DEFINITION,
                ConnectionClosurePacket.SERVER_DEFINITION,
                AuthorizationPacket.DEFINITION,
                AuthorizationResultPacket.DEFINITION,
                ProtocolVersionPacket.DEFINITION,
                ProtocolVersionSuccessPacket.DEFINITION
        );

        for (int i = 0; i < definitionsAndHandlers.size(); i++) {
            Pair<? extends PacketDefinition<?>, PacketHandler<?>> definitionAndHandler = definitionsAndHandlers.get(i);
            PacketDefinition<?> definition = definitionAndHandler.a();
            protocolVersion.versions()[i + RESERVED_PACKETS] = definition.version();
            protocolVersion.directions()[i + RESERVED_PACKETS] = definition.direction();
            definitions[i + RESERVED_PACKETS] = definition;
            handlers[i + RESERVED_PACKETS] = definitionAndHandler.b();
        }
    }

    private static void includeReservations(ProtocolVersion protocolVersion, PacketDefinition<?>[] definitions, PacketHandler<?>[] handlers, PacketDefinition<?>... reservations) {
        if(reservations.length > RESERVED_PACKETS) throw new IllegalArgumentException("More packets for reservation provided than this implementation supports");

        for (int i = 0; i < reservations.length; i++) {
            definitions[i] = reservations[i];
            handlers[i] = PacketHandler.blank();
            protocolVersion.versions()[i] = reservations[i].version();
            protocolVersion.directions()[i] = reservations[i].direction();
        }

        for (int i = reservations.length; i < RESERVED_PACKETS; i++) {
            protocolVersion.versions()[i] = -1;
            protocolVersion.directions()[i] = Direction.UNDEFINED;
        }
    }

    public @Nullable ServerAuthorizationHandler serverAuthorizationHandler() {
        return serverAuthorizationHandler;
    }

    public ProtocolVersion protocolVersion() {
        return protocolVersion;
    }

    public PacketDefinition<?> definition(int byId) {
        return definitions[byId];
    }

    public int findDefinitionId(PacketDefinition<?> definition) {
        int result = -1;
        for (int i = 0; i < definitions.length; i++) {
            if(definitions[i] == definition) {
                result = i;
                break;
            }
        }
        return result;
    }

    public PacketHandler<?> handler(int byId) {
        return handlers[byId];
    }

    public int findHandlerId(PacketHandler<?> handler) {
        int result = -1;
        for (int i = 0; i < handlers.length; i++) {
            if(handlers[i] == handler) {
                result = i;
                break;
            }
        }
        return result;
    }

    public <Instance extends Record, Definition extends PacketDefinition<Instance>> void write(ByteBuf output, Definition definition, Instance instance) {
        int id = findDefinitionId(definition);
        if(id == -1) throw new NoSuchElementException("This packet definition is not registered in that protocol");

        DataUtil.writeSequentially(
                output,
                ByteBuf::writeInt, definition,
                id, instance
        );
    }

    public <Instance extends Record, Definition extends PacketDefinition<Instance>> void writeAndFlush(Channel channel, Definition definition, Instance instance) {
        ByteBuf output = Unpooled.buffer();
        write(output, definition, instance);
        channel.writeAndFlush(output);
    }

    public ReadPacket readPacket(ByteBuf input) throws PacketProcessingException {
        int id = input.readInt();
        if(id < 0 || id >= definitions.length) throw new PacketProcessingException("packet " + id, new ArrayIndexOutOfBoundsException(id));

        PacketDefinition<?> definition = definitions[id];
        if(definition.direction() != direction) throw new PacketProcessingException("packet " + id + " has direction " + definition.direction() + ", but this protocol only accepts " + direction.name());

        return new ReadPacket(id, definition, definition.read(input));
    }

    public void tryToHandle(MusubiConnection connection, ByteBuf input) {
        tryToHandle(connection, readPacket(input));
    }

    public void tryToHandle(MusubiConnection connection, ReadPacket readPacket) {
        try {
            handlers[readPacket.packetId].handle(connection, cast(readPacket.definition), cast(readPacket.packetInstance));
        }
        catch (Throwable throwable) {
            throw new PacketProcessingException(throwable);
        }
    }

    private static <E> E cast(Object object) {
        return (E) object;
    }

    public static Protocol.Builder client() {
        return new Builder(Direction.CLIENT, null);
    }

    public static Protocol.Builder server(ServerAuthorizationHandler serverAuthorizationHandler) {
        return new Builder(Direction.SERVER, serverAuthorizationHandler);
    }

    public record ReadPacket(int packetId, PacketDefinition<?> definition, Record packetInstance) {}

    public static final class Builder {

        private final Direction acceptingDirection;
        private final @Nullable ServerAuthorizationHandler serverAuthorizationHandler;
        private final List<Pair<? extends PacketDefinition<?>, PacketHandler<?>>> definitionsAndHandlers;

        private boolean frozen;
        private Protocol result;

        public Builder(Direction acceptingDirection, @Nullable ServerAuthorizationHandler serverAuthorizationHandler) {
            this.acceptingDirection = acceptingDirection;
            this.serverAuthorizationHandler = serverAuthorizationHandler;
            this.definitionsAndHandlers = new ArrayList<>();
        }

        public <Instance extends Record> Builder add(PacketDefinition<Instance> definition) {
            if(frozen) throw new IllegalStateException("builder instance is frozen");
            definitionsAndHandlers.add(new Pair<>(definition, PacketHandler.blank()));
            return this;
        }

        public <Instance extends Record> Builder add(PacketDefinition<Instance> definition, PacketHandler<Instance> handler) {
            if(frozen) throw new IllegalStateException("builder instance is frozen");
            definitionsAndHandlers.add(new Pair<>(definition, handler));
            return this;
        }

        public Protocol build() {
            if(frozen) return result;
            frozen = true;
            result = new Protocol(acceptingDirection, serverAuthorizationHandler, definitionsAndHandlers);
            return result;
        }

    }

}
