package nia.chapter6;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/*
 * 使用SimpleChannelInboundHandler释放消息资源
 *
 * @Author Egan
 * @Date 2018/5/4
 **/
public class SimpleDiscardHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        //自动释放消息资源
    }
}
