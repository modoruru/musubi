package fun.modoru.musubi.builtin.data;

import fun.modoru.musubi.builtin.data.misc.ChatChannel;
import fun.modoru.musubi.builtin.data.server.GroupInvite;
import fun.modoru.musubi.builtin.data.user.NameColor;
import fun.modoru.musubi.builtin.data.user.Particle;
import fun.modoru.musubi.builtin.data.user.Subscription;
import fun.modoru.musubi.builtin.data.user.TeamRole;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.Codecs;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class UserDefinition {

    private static final AtomicInteger GLOBAL_INDEX = new AtomicInteger();
    private static final Map<Integer, UserProperty<?>> ID_TO_PROPERTY = new HashMap<>();

    // increment on changing something
    public static final int VERSION = Integer.MIN_VALUE;

    public static final UserProperty<Long>
            creationDate = define(Codecs.LONG),
            discordId = define(Codecs.LONG);

    public static final UserProperty<String> lastKnownUsername = define(Codecs.UTF_8);

    public static final UserProperty<Subscription> subscription = define(Subscription.CODEC);
    public static final UserProperty<TeamRole> teamRole = define(TeamRole.CODEC);

    public static final UserProperty<UUID> groupUuid = define(Codecs.UUID);

    public static final UserProperty<List<NameColor.AcquiredNameColor>> acquiredNameColors = define(Codecs.listCodec(NameColor.AcquiredNameColor.CODEC));
    public static final UserProperty<NameColor> currentNameColor = define(NameColor.CODEC);

    // Mostly properties
    public static final UserProperty<Particle> particle = define(Particle.CODEC);
    public static final UserProperty<Boolean>
            skipLobbyEnabled = define(Codecs.BOOLEAN, true),
            welcomeMessageEnabled = define(Codecs.BOOLEAN, true),
            personalMentionsEnabled = define(Codecs.BOOLEAN, true),
            globalChatEnabled = define(Codecs.BOOLEAN, true),
            receiveGroupInvitations = define(Codecs.BOOLEAN, true);
    public static final UserProperty<ChatChannel> preferredChatChannel = define(ChatChannel.CODEC, ChatChannel.LOCAL);
    public static final UserProperty<Theme> theme = define(Theme.CODEC, Theme.DEFAULT); // themes are coming to modoru :)

    // Active incoming invites
    public static final UserProperty<List<GroupInvite>> activeGroupInvites = define(Codecs.listCodec(GroupInvite.CODEC));

    public static final UserProperty<List<UUID>> ignoredUsers = define(Codecs.listCodec(Codecs.UUID));

    private UserDefinition() {}

    public static UserProperty<?> byId(int id) {
        return ID_TO_PROPERTY.get(id);
    }

    public static int length() {
        return GLOBAL_INDEX.get();
    }

    public static UserProperty<?>[] properties() {
        UserProperty<?>[] result = new UserProperty[length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ID_TO_PROPERTY.get(i);
        }
        return result;
    }

    private static <E> UserProperty<E> define(Codec<E> codec) {
        return define(codec, null);
    }

    private static <E> UserProperty<E> define(Codec<E> codec, @Nullable E defaultValue) {
        int index = GLOBAL_INDEX.getAndIncrement();

        UserProperty<E> property = new UserProperty<>(index, codec, defaultValue);
        ID_TO_PROPERTY.put(index, property);
        return property;
    }

}
