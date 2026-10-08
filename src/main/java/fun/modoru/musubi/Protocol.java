package fun.modoru.musubi;

import fun.modoru.musubi.packet.*;
import fun.modoru.musubi.packets.*;
import fun.modoru.musubi.util.DataUtil;
import fun.modoru.musubi.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class Protocol {

    public static final int RESERVED_PACKETS = 10;

    private final Flow flow;
    private final @Nullable ServerAuthorizationHandler serverAuthorizationHandler;
    private final ProtocolVersion protocolVersion;
    private final PacketDefinition<?>[] definitions;
    private final PacketHandler<?, ?>[] handlers;

    private Protocol(Flow flow, @Nullable ServerAuthorizationHandler serverAuthorizationHandler, List<Pair<? extends PacketDefinition<?>, PacketHandler<?, ?>>> definitionsAndHandlers) {
        this.flow = flow;
        this.serverAuthorizationHandler = serverAuthorizationHandler;

        int size = definitionsAndHandlers.size() + RESERVED_PACKETS;
        protocolVersion = new ProtocolVersion(
                new int[size],
                new Flow[size]
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
            Pair<? extends PacketDefinition<?>, PacketHandler<?, ?>> definitionAndHandler = definitionsAndHandlers.get(i);
            PacketDefinition<?> definition = definitionAndHandler.a();
            protocolVersion.versions()[i + RESERVED_PACKETS] = definition.version();
            protocolVersion.flows()[i + RESERVED_PACKETS] = definition.flow();
            definitions[i + RESERVED_PACKETS] = definition;
            handlers[i + RESERVED_PACKETS] = definitionAndHandler.b();
        }
    }

    private static void includeReservations(ProtocolVersion protocolVersion, PacketDefinition<?>[] definitions, PacketHandler<?, ?>[] handlers, PacketDefinition<?>... reservations) {
        if(reservations.length > RESERVED_PACKETS) throw new IllegalArgumentException("More packets for reservation provided than this implementation supports");

        for (int i = 0; i < reservations.length; i++) {
            definitions[i] = reservations[i];
            handlers[i] = PacketHandler.blank();
        }

        for (int i = reservations.length; i < RESERVED_PACKETS; i++) {
            protocolVersion.versions()[i] = -1;
            protocolVersion.flows()[i] = Flow.UNDEFINED;
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

    public PacketHandler<?, ?> handler(int byId) {
        return handlers[byId];
    }

    public int findHandlerId(PacketHandler<?, ?> handler) {
        int result = -1;
        for (int i = 0; i < handlers.length; i++) {
            if(handlers[i] == handler) {
                result = i;
                break;
            }
        }
        return result;
    }

    public <Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> void write(ByteBuf output, Definition definition, Instance instance) {
        int id = findDefinitionId(definition);
        if(id == -1) throw new NoSuchElementException("This packet definition is not registered in that protocol");

        DataUtil.writeSequentially(
                output,
                ByteBuf::writeInt, definition,
                id, instance
        );
    }

    public <Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> void writeAndFlush(Channel channel, Definition definition, Instance instance) {
        ByteBuf output = Unpooled.buffer();
        write(output, definition, instance);
        channel.writeAndFlush(output);
    }

    public ReadPacket readPacket(ByteBuf input) throws PacketProcessingException {
        int id = input.readInt();
        if(id < 0 || id >= definitions.length) throw new PacketProcessingException("packet " + id, new ArrayIndexOutOfBoundsException(id));

        PacketDefinition<?> definition = definitions[id];
        if(definition.flow() != flow) throw new PacketProcessingException("packet " + id + " has flow " + definition.flow() + ", but this protocol only accepts " + flow.name());

        return new ReadPacket(id, definition, definition.read(input));
    }

    public void tryToHandle(ChannelHandlerContext channelHandlerContext, ByteBuf input) {
        tryToHandle(channelHandlerContext, readPacket(input));
    }

    public void tryToHandle(ChannelHandlerContext channelHandlerContext, ReadPacket readPacket) {
        try {
            handlers[readPacket.packetId].handle(channelHandlerContext, cast(readPacket.definition), cast(readPacket.packetInstance));
        }
        catch (Throwable throwable) {
            throw new PacketProcessingException(throwable);
        }
    }

    private static <E> E cast(Object object) {
        return (E) object;
    }

    public static Protocol.Builder client() {
        return new Builder(Flow.CLIENT, null);
    }

    public static Protocol.Builder server(ServerAuthorizationHandler serverAuthorizationHandler) {
        return new Builder(Flow.SERVER, serverAuthorizationHandler);
    }

    public record ReadPacket(int packetId, PacketDefinition<?> definition, PacketInstance packetInstance) {}

    public static final class Builder implements AutoCloseable {

        private final Flow flow;
        private final @Nullable ServerAuthorizationHandler serverAuthorizationHandler;
        private final List<Pair<? extends PacketDefinition<?>, PacketHandler<?, ?>>> definitionsAndHandlers;

        private boolean frozen;
        private Protocol result;

        public Builder(Flow flow, @Nullable ServerAuthorizationHandler serverAuthorizationHandler) {
            this.flow = flow;
            this.serverAuthorizationHandler = serverAuthorizationHandler;
            this.definitionsAndHandlers = new ArrayList<>();
        }

        public <Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> Builder add(Definition definition, PacketHandler<Instance, Definition> handler) {
            if(frozen) throw new IllegalStateException("builder instance is frozen");
            definitionsAndHandlers.add(new Pair<>(definition, handler));
            return this;
        }

        public Protocol build() {
            if(frozen) return result;
            close();
            return result;
        }

        @Override
        public void close() {
            if(frozen) throw new IllegalStateException("builder instance is frozen");
            frozen = true;
            result = new Protocol(flow, serverAuthorizationHandler, definitionsAndHandlers);
        }

    }

}
