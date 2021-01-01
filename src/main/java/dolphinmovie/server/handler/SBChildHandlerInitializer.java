package dolphinmovie.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class SBChildHandlerInitializer extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpContentCompressor());
		pipeline.addLast(new HttpObjectAggregator(512*1024));
		pipeline.addLast(new HttpRequestHandler());
		pipeline.addLast(new TheaterRequestHandler());
		pipeline.addLast(new IDConfirmHandler());
		pipeline.addLast(new LoginRequestHandler());
		pipeline.addLast(new LogoutRequestHandler());
		pipeline.addLast(new JoinRequestHandler());
	}
	
}
