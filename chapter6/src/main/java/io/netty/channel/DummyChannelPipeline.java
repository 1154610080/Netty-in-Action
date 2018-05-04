package io.netty.channel;

public class DummyChannelPipeline extends DefaultChannelPipeline {
    public static ChannelPipeline DUMMY_INSTANCE = new DummyChannelPipeline(null);
    public DummyChannelPipeline(Channel channel){
        super(channel);
    }
}
