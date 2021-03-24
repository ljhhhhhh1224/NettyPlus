package com.Ljh.netty.dubborpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    private  ChannelHandlerContext context;//上下文
    private String result;//返回的结果
    private String para;//客户端调用方法时传入的参数

    //与服务器的链接创建成果后会被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;//因为在其它方法会使用到ctx
    }

    //收到数据后会被调用方法
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        notify();//唤醒等待的线程
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    //被代理对象调用，发送数据给服务器->wait(channelRead) 等待被唤醒
    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(para);
        wait();//等待channelActive获取到服务器的结果后唤醒我
        return result;//服务器返回的结果
    }

    void setPara(String para){
        this.para = para;
    }
}
