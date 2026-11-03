package fun.modoru.musubi.peer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MusubiFrameDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_SIZE = 10 * 1024 * 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> output) {
        if (input.readableBytes() < 4) return;

        input.markReaderIndex();
        int length = input.readInt();

        if (length < 4 || length > MAX_FRAME_SIZE) {
            throw new IllegalStateException("Invalid musubi frame length: " + length);
        }

        if (input.readableBytes() < length) {
            input.resetReaderIndex();
            return;
        }

        output.add(input.readRetainedSlice(length));
    }
}