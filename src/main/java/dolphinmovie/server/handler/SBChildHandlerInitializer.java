package dolphinmovie.server.handler;

import javax.net.ssl.SSLEngine;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

public class SBChildHandlerInitializer extends ChannelInitializer<Channel> {
	
	private final SslContext context;
	
	public SBChildHandlerInitializer(SslContext context) {
		this.context = context;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if(context != null) {
			SSLEngine engine = context.newEngine(ch.alloc());
			engine.setNeedClientAuth(false);
			pipeline.addLast(new SslHandler(engine));
		}
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
