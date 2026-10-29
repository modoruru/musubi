package fun.modoru.musubi.builtin.packets.server;

import fun.modoru.musubi.builtin.data.server.Group;
import fun.modoru.musubi.builtin.packets.WaitingPacket;
import fun.modoru.musubi.data.Codec;
import fun.modoru.musubi.data.FixedCodec;
import fun.modoru.musubi.packet.Direction;
import fun.modoru.musubi.packet.PacketDefinition;
import fun.modoru.musubi.util.DataUtil;
import io.netty.buffer.ByteBuf;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record EditGroupPacket(UUID waitingUuid, GroupPatch groupPatch) implements WaitingPacket {

    public static final PacketDefinition<EditGroupPacket> DEFINITION = new PacketDefinition<>(
            Direction.SERVER,
            Integer.MIN_VALUE,
            input -> new EditGroupPacket(DataUtil.readUuid(input), GroupPatch.CODEC.read(input)),
            (output, element) -> DataUtil.writeSequentially(
                    output,
                    DataUtil::writeUuid,
                    GroupPatch.CODEC,
                    element.waitingUuid, element.groupPatch
            )
    );

    public static GroupPatchBuilder groupPatch(Group group) {
        return new GroupPatchBuilder(group);
    }

    public static final class GroupPatchBuilder {

        private final Group group;

        private @Nullable UUID newOwner;
        private @Nullable String newName;
        private @Nullable Integer newIconColor;
        private @Nullable Set<UUID> newMembers, kickedMembers;

        private GroupPatchBuilder(Group group) {
            this.group = group;
        }

        public GroupPatchBuilder changeOwner(UUID newOwner) {
            this.newOwner = newOwner;
            return this;
        }

        public GroupPatchBuilder changeName(String newName) {
            this.newName = newName;
            return this;
        }

        public GroupPatchBuilder changeIconColor(int newIconColor) {
            this.newIconColor = newIconColor;
            return this;
        }

        public GroupPatchBuilder addMember(UUID newMember) {
            if(newMembers == null) newMembers = new HashSet<>();
            newMembers.add(newMember);
            return this;
        }

        public GroupPatchBuilder kickMember(UUID kickedMember) {
            if(kickedMembers == null) kickedMembers = new HashSet<>();
            kickedMembers.add(kickedMember);
            return this;
        }

        public GroupPatch build() {
            Set<UUID> currentMembers = new HashSet<>(group.members());

            if (newMembers != null && newMembers.contains(null))
                throw new IllegalArgumentException("New members cannot contain null");
            if (kickedMembers != null && kickedMembers.contains(null))
                throw new IllegalArgumentException("Kicked members cannot contain null");

            if (newMembers != null && kickedMembers != null) {
                Set<UUID> conflictingMembers = new HashSet<>(newMembers);
                conflictingMembers.retainAll(kickedMembers);

                if (!conflictingMembers.isEmpty())
                    throw new IllegalArgumentException("Cannot add and kick the same members: " + conflictingMembers);
            }

            if (newMembers != null) {
                for (UUID member : newMembers) {
                    if (currentMembers.contains(member))
                        throw new IllegalArgumentException("Player is already a group member: " + member);
                }
            }

            if (kickedMembers != null) {
                for (UUID member : kickedMembers) {
                    if (!currentMembers.contains(member))
                        throw new IllegalArgumentException("Player is not a group member: " + member);
                }
            }

            Set<UUID> resultingMembers = new HashSet<>(currentMembers);

            if (kickedMembers != null) resultingMembers.removeAll(kickedMembers);
            if (newMembers != null) resultingMembers.addAll(newMembers);

            if (resultingMembers.isEmpty())
                throw new IllegalArgumentException("Group must have at least one member");

            UUID resultingOwner = newOwner != null ? newOwner : group.ownerUuid();
            if (!resultingMembers.contains(resultingOwner)) {
                throw new IllegalArgumentException("Group owner must be a member of the resulting group: " + resultingOwner);
            }

            if (newName != null && newName.isBlank())
                throw new IllegalArgumentException("Group name cannot be blank");

            if (newOwner == null && newName == null && newIconColor == null && (newMembers == null || newMembers.isEmpty()) && (kickedMembers == null || kickedMembers.isEmpty()))
                throw new IllegalStateException("Group patch contains no changes");

            return new GroupPatch(
                    group.groupUuid(),
                    newOwner,
                    newName,
                    newIconColor,
                    newMembers == null ? null : Set.copyOf(newMembers),
                    kickedMembers == null ? null : Set.copyOf(kickedMembers)
            );
        }

    }

    public record GroupPatch(
            UUID groupUuid,
            @Nullable UUID newOwner, @Nullable String newName, @Nullable Integer newIconColor,
            @Nullable Set<UUID> newMembers, @Nullable Set<UUID> kickedMembers
    ) {

        public static final Codec<GroupPatch> CODEC = new FixedCodec<>(
                input -> new GroupPatch(
                        DataUtil.readUuid(input),
                        DataUtil.readNullable(input, DataUtil::readUuid),
                        DataUtil.readNullable(input, DataUtil::readUtf8),
                        DataUtil.readNullable(input, ByteBuf::readInt),
                        DataUtil.readNullable(input, input1 -> DataUtil.readSet(input1, DataUtil::readUuid)),
                        DataUtil.readNullable(input, input1 -> DataUtil.readSet(input1, DataUtil::readUuid))
                ),
                (output, element) -> DataUtil.writeSequentially(
                        output,
                        DataUtil::writeUuid,
                        DataUtil.nullableWriter(DataUtil::writeUuid),
                        DataUtil.nullableWriter(DataUtil::writeUtf8),
                        DataUtil.nullableWriter(ByteBuf::writeInt),
                        DataUtil.nullableWriter(DataUtil.collectionWriter(DataUtil::writeUuid)),
                        DataUtil.nullableWriter(DataUtil.collectionWriter(DataUtil::writeUuid)),
                        element.groupUuid, element.newOwner, element.newName, element.newIconColor, element.newMembers, element.kickedMembers
                )
        );

    }

}
