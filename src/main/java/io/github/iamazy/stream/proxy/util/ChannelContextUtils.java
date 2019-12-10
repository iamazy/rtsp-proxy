package io.github.iamazy.stream.proxy.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author iamazy
 * @date 2019/11/29
 * @descrition
 **/
public class ChannelContextUtils {

    public static String clientIp(ChannelHandlerContext ctx){
        InetSocketAddress remoteAddress=(InetSocketAddress) ctx.channel().remoteAddress();
        return remoteAddress.getHostString();
    }
}
