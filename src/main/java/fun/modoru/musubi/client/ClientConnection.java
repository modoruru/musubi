package fun.modoru.musubi.client;

import fun.modoru.musubi.Protocol;
import fun.modoru.musubi.peer.MusubiConnection;
import io.netty.channel.Channel;

public record ClientConnection(Channel channel, Protocol protocol) implements MusubiConnection {}
