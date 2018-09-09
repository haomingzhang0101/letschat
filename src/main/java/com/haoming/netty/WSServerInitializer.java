package com.haoming.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Since websocket is based on Http, a http encoder/decoder is needed.
        pipeline.addLast(new HttpServerCodec());

        // Add support for large data streams.
        pipeline.addLast(new ChunkedWriteHandler());

        pipeline.addLast(new HttpObjectAggregator(1024*64));

        //=====================Support http protocol==========================

        /**
         * Assign the router for users to connect.
         * This handler does all the heavy lifting for you to run a websocket server.
         * It takes care of websocket handshaking as well as processing of control frames (Close, Ping, Pong)
         * ping + pong = heartbeat.
         * Everything is transmitted by frames in netty. Different data types correspond to different frames.
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        pipeline.addLast(new ChatHandler());
    }
}
