package io.github.iamazy.stream.proxy.proxy;

import io.github.iamazy.stream.proxy.model.RtspProxy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.rtsp.RtspDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @author iamazy
 * @date 2019/10/8
 * @descrition
 **/
@ChannelHandler.Sharable
public class IceProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final RtspProxy proxyClient;

    IceProxyInitializer(RtspProxy proxyClient) {
        this.proxyClient = proxyClient;
    }


    @Override
    protected void initChannel(SocketChannel socketChannel) {

        socketChannel.pipeline()
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpServerExpectContinueHandler())
                .addLast(new HttpContentCompressor())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new ChunkedWriteHandler())
                .addLast(new IceProxyFrontendHandler(proxyClient));
    }
}
