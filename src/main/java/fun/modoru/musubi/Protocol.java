package fun.modoru.musubi;

import fun.modoru.musubi.packet.*;
import fun.modoru.musubi.util.Pair;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class Protocol {

    private final Flow flow;
    private final ProtocolVersion protocolVersion;
    private final PacketDefinition<?>[] definitions;
    private final PacketHandler<?, ?>[] handlers;

    private Protocol(Flow flow, List<Pair<? extends PacketDefinition<?>, PacketHandler<?, ?>>> definitionsAndHandlers) {
        this.flow = flow;

        int size = definitionsAndHandlers.size() + 2; // reservation for scheme and authorization
        protocolVersion = new ProtocolVersion(
                new int[size],
                new Flow[size]
        );
        definitions = new PacketDefinition[size];
        handlers = new PacketHandler[size];

        for (int i = 0; i < definitionsAndHandlers.size(); i++) {
            Pair<? extends PacketDefinition<?>, PacketHandler<?, ?>> definitionAndHandler = definitionsAndHandlers.get(i);
            PacketDefinition<?> definition = definitionAndHandler.a();
            protocolVersion.versions()[i + 2] = definition.version();
            protocolVersion.flows()[i + 2] = definition.flow();
            definitions[i + 2] = definition;
            handlers[i + 2] = definitionAndHandler.b();
        }
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

        output.writeInt(id);
        definition.write(output, instance);
    }

    public void tryToHandle(ByteBuf input) {
        int id = input.readInt();
        if(id < 0 || id >= definitions.length) throw new PacketProcessingException("packet " + id, new ArrayIndexOutOfBoundsException(id));

        PacketDefinition<?> definition = definitions[id];
        if(definition.flow() != flow) throw new PacketProcessingException("packet " + id + " has flow " + definition.flow() + ", but this protocol only accepts " + flow.name());

        try {
            PacketInstance packetInstance = definition.read(input);
            handlers[id].handle(cast(definition), cast(packetInstance));
        }
        catch (Throwable throwable) {
            throw new PacketProcessingException(throwable);
        }
    }

    private static <E> E cast(Object object) {
        return (E) object;
    }

    public static Protocol.Builder builder(Flow flow) {
        return new Builder(flow);
    }

    public static final class Builder implements AutoCloseable {

        private final Flow flow;
        private final List<Pair<? extends PacketDefinition<?>, PacketHandler<?, ?>>> definitionsAndHandlers;
        private boolean frozen;
        private Protocol result;

        public Builder(Flow flow) {
            this.flow = flow;
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
            result = new Protocol(flow, definitionsAndHandlers);
        }

    }

}
