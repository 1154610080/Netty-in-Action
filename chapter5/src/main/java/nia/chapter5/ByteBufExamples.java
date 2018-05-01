package nia.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.ByteBuffer;
import java.util.Random;

/*
 * ByteBuf使用模式的样例
 *
 * @Author Egan
 * @Date 2018/5/2
 **/
public class ByteBufExamples {
    final static Random random = new Random();
    final static ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);
    final static Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /*
     * 支撑数组(backing array)模式
     *   数据存放在JVM的堆缓冲区
     *
     * @date 2018/5/2 0:29
     * @param []
     * @return void
     */
    public static void headBuffer(){
        ByteBuf bytebuf = BYTE_BUF_FROM_SOMEWHERE;
        //检查是否有一个支撑数组
        if(bytebuf.hasArray()){
            //获得对该数组的引用
            byte[] array = bytebuf.array();
            //计算第一个字节偏移量
            int offset = bytebuf.arrayOffset() + bytebuf.readerIndex();
            //获取可读字节量
            int len = bytebuf.readableBytes();
            //调用自定义方法
            handleBuff(array, offset, len);
        }
    }


    /*
     * 直接缓冲区模式
     *
     * @date 2018/5/2 0:37
     * @param []
     * @return void
     */
    public static void directBuffer(){
        ByteBuf directBuf = BYTE_BUF_FROM_SOMEWHERE;
        if(!directBuf.hasArray()){
            int len = directBuf.readableBytes();
            byte[] array = new byte[len];
            directBuf.getBytes(directBuf.readerIndex(), array);
            handleBuff(array, 0, len);
        }
    }


    /*
     * 复合缓冲区模式(使用JDK的byteBuffer实现)
     *
     * @Author Egan
     * @Date 2018/5/2
     **/
    public static void byteBufferComposite(ByteBuffer header, ByteBuffer body){

        //创建一个数组保存header和body
        ByteBuffer[] buffers = new ByteBuffer[]{header, body};

        //创建一个新的ByteBuffer并分配空间
        ByteBuffer message = ByteBuffer.allocate(header.remaining() + body.remaining());

        message.put(header);
        message.put(body);
        message.flip();
    }

    /*
     * 复合缓冲区模式(使用netty的ByteBuf实现)
     *
     * @date 2018/5/2 0:48
     * @param []
     * @return void
     */
    public static void byteBufComposite(){

        CompositeByteBuf messages = Unpooled.compositeBuffer();

        //支撑或者直接都可以
        ByteBuf headerBuf = BYTE_BUF_FROM_SOMEWHERE;
        ByteBuf bodyBuf = BYTE_BUF_FROM_SOMEWHERE;

        messages.addComponents(headerBuf, bodyBuf);
        //...若干操作
        messages.removeComponent(0);

        for(ByteBuf buf : messages){
            System.out.println(buf.toString());
        }


    }


    /*
     * 访问CompositeByteBuf中的数据
     *
     * @Author Egan
     * @Date 2018/5/2
     **/
    public static void byteBufCompositeArray(){
        CompositeByteBuf message = Unpooled.compositeBuffer();
        int len = message.readableBytes();
        byte[] array = new byte[len];
        message.getBytes(message.readerIndex(), array);
        handleBuff(array, 0, len);
    }


    public static void handleBuff(byte[] buff, int offset, int len){}
}
