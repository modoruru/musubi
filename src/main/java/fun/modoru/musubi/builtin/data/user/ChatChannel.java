package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;

public enum ChatChannel {

    GLOBAL,
    LOCAL,
    GROUP;

    public static final Codec<ChatChannel> CODEC = Codecs.enumCodec(ChatChannel.class);

}
