import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/*
 * 简单的异步网络编程
 *
 * @Author Egan
 * @Date 2018/4/30
 **/
public class PlainNioServer {
    public void server(int port) throws IOException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        //设置该channel为非阻塞模式
        serverSocket.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(port);
        serverSocket.bind(address);
        //打开selector处理channel
        Selector selector = Selector.open();
        //将serverSocket注册到selector以接受连接
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        final ByteBuffer msg = ByteBuffer.wrap("Hi\r\b".getBytes());
        while (true){
            //等待需要处理的事件，在下一个事件传入前将一直阻塞
            try {
                selector.select();
            }catch (IOException e){
                e.printStackTrace();
                break;
            }
            //获取所有接收事件的selection-key实例
            Set<SelectionKey>readKeys = selector.selectedKeys();
            Iterator<SelectionKey>iterator = readKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    //检查事件是否是一个新的已就绪且可接受的连接
                    if(key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        //接受客户端并将它注册到选择器
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("接受来自 " + client + " 的连接");
                    }
                    //检查套接字是否已经准备好写数据
                    if(key.isWritable()){
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        while (buffer.hasRemaining()){
                            if(client.write(buffer) == 0)
                                break;
                        }
                        client.close();
                    }
                }catch (IOException e){
                    key.cancel();
                    try {
                        key.channel().close();
                    }catch (IOException cex){
                        cex.printStackTrace();
                    }
                }
            }
        }
    }
}
