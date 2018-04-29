package nia.chapter2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/*   服务端主类
 *
 * @author EganChen
 * @date 2018/4/14 16:02
 */
public class EchoServer {

    private final int port;

    EchoServer(int port){
        this.port = port;
    }

    public void start() throws InterruptedException {

        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建服务器引导
            ServerBootstrap b = new ServerBootstrap();
            //因为使用的是NIO传输，所以用NioEventLoopGroup接受和处理连接
            b.group(group)
                    //指定通道类型
                    .channel(NioServerSocketChannel.class)
                    //将本地地址设为一个具有选定端口的InetSocketAddress，
                    // 服务器绑定到该地址并开始监听新请求
                    .localAddress(new InetSocketAddress(port))
                    //当一个新连接被接受时，一个新的子通道将被创建
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel)
                                throws Exception {
                            //用EchoServerHandler处理入站请求的通知
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
            //异步绑定服务器，调用sync阻塞当前线程直到绑定完成
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() +
                    " started and listening for connections on " + f.channel().localAddress());
            //获取Chanel的FutureClose，同样调用sync直到它完成
            f.channel().closeFuture().sync();
        }finally{
            //关闭EventLoopGroup，并释放所有资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {

        //如果端口值不正确，抛出NumberFormatException异常
        if(args.length != 1){
            System.err.println(
                    "Usage" + EchoServer.class.getSimpleName() + "<port>"
            );
            return;
        }

        //否则设置端口值
        int port = Integer.parseInt(args[0]);
        //调用start方法;
        new EchoServer(port).start();
    }

}
