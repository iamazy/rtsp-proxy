package io.github.iamazy.stream.proxy.proxy;

import io.github.iamazy.stream.proxy.model.RtspProxy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * @author iamazy
 * @date 2019/10/9
 * @descrition
 **/
public class IceProxy extends Thread {

    private final RtspProxy proxy;

    public IceProxy(RtspProxy proxy){
        this.proxy =proxy;
        this.setName(proxy.getStreamId());
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new IceProxyInitializer(proxy))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(proxy.getLocalPort()).sync().channel().closeFuture().sync();
        }catch (InterruptedException e){
            System.out.println("线程["+proxy.getStreamId()+"]关闭,端口:["+proxy.getLocalPort()+"]解除占用");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
