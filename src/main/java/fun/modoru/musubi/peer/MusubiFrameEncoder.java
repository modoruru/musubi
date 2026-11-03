package fun.modoru.musubi.peer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MusubiFrameEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf input, ByteBuf output) {
        int length = input.readableBytes();

        output.writeInt(length);
        output.writeBytes(input, input.readerIndex(), length);
    }

}