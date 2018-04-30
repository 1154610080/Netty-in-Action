import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*
 * Channel操作的两个例子
 *
 * @date 2018/4/30 21:17  
 * @param   
 * @return   
 */  
public class ChannelOperationExamples {

    final static Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /*
     * 向channel写入消息
     * 
     * @date 2018/4/30 21:23  
     * @param []  
     * @return void  
     */  
    public static void writeToChannel(){
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        ByteBuf buf = Unpooled.copiedBuffer("test data", Charset.forName("UTF-8"));
        ChannelFuture future = channel.writeAndFlush(buf);
        future.addListener(
                new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        //写操作完成，且无错误发生
                        if(channelFuture.isSuccess()){
                            System.out.println("Write Successful");
                        }else{
                            System.err.println("Write Error");
                            future.cause().printStackTrace();
                        }
                    }
                }
        );
    }
    
    /*
     * 多个线程共享一个channel
     * 
     * @Author Egan
     * @Date 2018/4/30
     **/
    public static void writeToManyChanel(){
        Channel channel = CHANNEL_FROM_SOMEWHERE;
        //retain()用于对引用计数，保证累加操作是线程安全的
        ByteBuf buf = Unpooled.copiedBuffer("test data", Charset.forName("UTF-8")).retain();

        Runnable writer = new Runnable() {
            @Override
            public void run() {
                channel.writeAndFlush(buf.duplicate());
            }
        };

        //获取线程池executor的引用
        Executor executor = Executors.newCachedThreadPool();

        //递交写任务到线程池以便在某个线程中执行
        executor.execute(writer);

        //递交另一个写任务以便在另一个线程池中执行
        executor.execute(writer);
    }
}
