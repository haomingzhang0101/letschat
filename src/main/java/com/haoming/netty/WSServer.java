package com.haoming.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class WSServer {

    private EventLoopGroup mainGroup;
    private EventLoopGroup subGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture future;

    private WSServer() {
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerInitializer());
    }

    public static WSServer getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    public void start() {
        this.future = this.serverBootstrap.bind(8889);
        System.err.println("Netty websocket server boots successfully");
    }

    private enum Singleton {
        INSTANCE;

        private WSServer singleton;

        // JVM will ensure that this method will only be called once.
        // Instance will only be initialized when get called.
        Singleton() {
            singleton = new WSServer();
        }

        public WSServer getInstance() {
            return singleton;
        }
    }

}
