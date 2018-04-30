import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * 普通的阻塞网络编程
 *
 * @date 2018/4/30 16:04  
 * @param   
 * @return   
 */  
public class PlainOioServer {

    public void serve(int port) throws IOException {

        //将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(port);

        try {
            while (true){
                //阻塞直到建立连接
                final Socket client = socket.accept();
                System.out.println("接受来自 " + socket + " 的连接");

                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                OutputStream out;
                                try{
                                    out = client.getOutputStream();
                                    //向客户端发送消息
                                    out.write("Hi\r\n".getBytes(Charset.forName("UTF-8")));
                                    out.flush();
                                    //关闭连接
                                    client.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }finally {
                                    try{
                                        client.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                ).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
