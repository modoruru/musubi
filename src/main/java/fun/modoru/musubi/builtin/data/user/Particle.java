package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;

public enum Particle {

    CHERRY,
    PALE_OAK;

    public static final Codec<Particle> CODEC = Codecs.enumCodec(Particle.class);

}
