package com.haoming.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;


/**
 * Handler for dealing with message.
 * TextWebSocketFrame is the class for handling text, a frame acts as a message carrier.
 * @author zhanghm
 * @date 2018-09-01 01:51
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // Initialize a ChannelGroup to record and maintain all client channels.
    private static ChannelGroup clients =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // Receive the message from the client.
        String content = msg.text();
        System.out.println("Received message: " + content);

        for (Channel channel : clients) {
            channel.writeAndFlush(
                    new TextWebSocketFrame("Server receives message at "
                            + LocalDateTime.now() + ", content: " + content));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // When this method gets triggered, the channelGroup will automatically remove the correspondent channel.
        System.out.println("Client disconnected, the long id is" + ctx.channel().id().asLongText());
        System.out.println("Client disconnected, the short id is" + ctx.channel().id().asShortText());
    }
}
