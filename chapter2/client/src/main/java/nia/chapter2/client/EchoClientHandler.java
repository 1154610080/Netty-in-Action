package nia.chapter2.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/*   客户端的数据处理类
 *
 * @author EganChen
 * @date 2018/4/14 16:15
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    //当被通知Channel是活跃的时候，发送一条消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rock!", CharsetUtil.UTF_8));
    }

    //打印接收的数据，注意，服务器发送的信息可能会被分块接收，接收一条信息可能要多次调用channelRead0
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf)
            throws Exception {
        System.out.println("Client receive " + byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        cause.printStackTrace();
        ctx.close();
    }

}
