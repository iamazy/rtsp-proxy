package io.github.iamazy.stream.proxy.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author iamazy
 * @date 2019/10/8
 * @descrition
 **/
public class IceProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    public IceProxyBackendHandler(Channel inboundChannel){
        this.inboundChannel=inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) channelFuture -> {
            if(channelFuture.isSuccess()){
                ctx.channel().read();
            }else{
                ctx.channel().close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        IceProxyFrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        IceProxyFrontendHandler.closeOnFlush(ctx.channel());
    }
}
