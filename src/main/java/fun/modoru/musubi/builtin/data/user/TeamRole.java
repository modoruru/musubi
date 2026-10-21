package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;

public enum TeamRole {

    MEDIA,

    JUNIOR_AGENT,
    AGENT,
    REPORT_AGENT,
    SENIOR_AGENT,

    SUPERVISOR,

    ADMINISTRATOR,
    TECHNICAL_ADMINISTRATOR;

    public static final Codec<TeamRole> CODEC = Codecs.enumCodec(TeamRole.class);

    public boolean hasPermission(TeamRole teamRole) {
        return this.ordinal() >= teamRole.ordinal();
    }

}
