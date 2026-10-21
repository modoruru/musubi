package fun.modoru.musubi;

import fun.modoru.musubi.packet.Flow;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.packet.PacketHandler;
import fun.modoru.musubi.packet.PacketInstance;
import fun.modoru.musubi.util.Pair;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BuiltInProtocol {

    public static final List<? extends PacketDefinition<?>> DEFINITIONS = List.of(

    );

    private BuiltInProtocol() {}

    public static Builder client() {
        return new Builder(Flow.CLIENT, null);
    }

    public static Builder server(ServerAuthorizationHandler serverAuthorizationHandler) {
        return new Builder(Flow.SERVER, serverAuthorizationHandler);
    }

    public static final class Builder {

        private final Flow acceptingFlow;
        private final @Nullable ServerAuthorizationHandler serverAuthorizationHandler;
        private final Map<PacketDefinition<?>, PacketHandler<?, ?>> definitionsToHandlers;

        private boolean frozen;
        private Protocol result;

        private Builder(Flow acceptingFlow, @Nullable ServerAuthorizationHandler serverAuthorizationHandler) {
            this.acceptingFlow = acceptingFlow;
            this.serverAuthorizationHandler = serverAuthorizationHandler;
            this.definitionsToHandlers = new HashMap<>();
        }

        public <Instance extends Record & PacketInstance, Definition extends PacketDefinition<Instance>> Builder defineHandler(Definition definition, PacketHandler<Instance, Definition> handler) {
            if(acceptingFlow.oppositeFlow() == definition.flow())
                throw new IllegalArgumentException(String.format("You're trying to define handler for packet that has flow \"%s\", but this builder only accepts handlers for \"%s\".", definition.flow().name(), acceptingFlow));
            if(!DEFINITIONS.contains(definition))
                throw new IllegalArgumentException("No such definition in the built-in protocol.");

            definitionsToHandlers.put(definition, handler);
            return this;
        }

        public Protocol build() {
            if(frozen) return result;

            List<Pair<? extends PacketDefinition<?>, PacketHandler<?, ?>>> definitionsAndHandlers = new ArrayList<>();
            PacketHandler<?, ?> blank = PacketHandler.blank();
            for (PacketDefinition<?> definition : DEFINITIONS) {
                PacketHandler<?, ?> packetHandler = definitionsToHandlers.get(definition);
                if(definition.flow() == acceptingFlow) {
                    if(packetHandler == null) throw new IllegalStateException("Some handlers are missing.");
                }
                else packetHandler = blank;

                definitionsAndHandlers.add(new Pair<>(definition, packetHandler));
            }

            frozen = true;
            result = new Protocol(acceptingFlow, serverAuthorizationHandler, definitionsAndHandlers);
            return result;
        }

    }

}
