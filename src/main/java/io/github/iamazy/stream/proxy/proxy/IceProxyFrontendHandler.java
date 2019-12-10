package io.github.iamazy.stream.proxy.proxy;

import io.github.iamazy.stream.proxy.cons.CoreConstants;
import io.github.iamazy.stream.proxy.model.RtspProxy;
import io.github.iamazy.stream.proxy.util.ChannelContextUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;

import java.util.Date;


/**
 * @author iamazy
 * @date 2019/10/8
 * @descrition
 **/
public class IceProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private final RtspProxy proxyClient;

    private Channel outboundChannel;

    private EmbeddedChannel embeddedChannel;

    public IceProxyFrontendHandler(RtspProxy proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("时间:"+CoreConstants.DATE_TIME_FORMAT.format(new Date())+",来访Ip:"+ ChannelContextUtils.clientIp(ctx));
        final Channel inboundChannel = ctx.channel();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new IceProxyBackendHandler(inboundChannel))
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture future = bootstrap.connect(this.proxyClient.getRemoteIp(), this.proxyClient.getRemotePort());
        outboundChannel = future.channel();
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (future.isSuccess()) {
                inboundChannel.read();
            } else {
                inboundChannel.close();
            }
        });
        this.embeddedChannel = new EmbeddedChannel(new HttpRequestEncoder());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (outboundChannel.isActive()) {
            if (msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) msg;
                handleRtsp(request);
                if (embeddedChannel.isOpen()) {
                    embeddedChannel.writeOutbound(request);
                    msg = embeddedChannel.readOutbound();
                }
            }
            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    ctx.channel().read();
                } else {
                    channelFuture.channel().close();
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
        if (embeddedChannel != null) {
            closeOnFlush(embeddedChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
        closeOnFlush(embeddedChannel);
    }

    static void closeOnFlush(Channel channel) {
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleRtsp(HttpRequest request) {
        String[] items = request.uri().split("/");
        if (items.length > 1) {
            RtspProxy rtspProxy = CoreConstants.RTSP_PROXY_CACHE.get(items[items.length - 1]);
            if (rtspProxy != null) {
                request.setUri(rtspProxy.getSrcUrl());
            }
        }
    }
}
