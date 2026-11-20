package fun.modoru.musubi.builtin.data.user;

import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public enum TeamRole {

    MEDIA,

    JUNIOR_AGENT (null, ModerationPermission.WARN, ModerationPermission.MUTE, ModerationPermission.USER_INSPECTION),
    AGENT        (JUNIOR_AGENT, ModerationPermission.BAN),
    REPORT_AGENT (AGENT,        ModerationPermission.GAMEPLAY_ADVANCED_INSPECTION),
    SENIOR_AGENT (REPORT_AGENT, ModerationPermission.GAMEPLAY_ROLLBACK),

    SUPERVISOR   (SENIOR_AGENT, ModerationPermission.REMOVE_PUNISHMENT),

    ADMINISTRATOR(SUPERVISOR,   ModerationPermission.ALL);

    public static final Codec<TeamRole> CODEC = Codecs.enumCodec(TeamRole.class);

    private final @Nullable TeamRole parent;
    private final Set<ModerationPermission> permissions;

    TeamRole() {
        this(null, Set.of());
    }

    TeamRole(@Nullable TeamRole parent, ModerationPermission... permissions) {
        this(parent, Set.of(permissions));
    }

    TeamRole(@Nullable TeamRole parent, Set<ModerationPermission> permissions) {
        this.parent = parent;
        this.permissions = permissions;
    }

    public boolean hasPermission(ModerationPermission permission) {
        TeamRole teamRole = this;
        while (teamRole != null) {
            if(teamRole.permissions.contains(permission)) return true;
            teamRole = teamRole.parent;
        }
        return false;
    }

}
