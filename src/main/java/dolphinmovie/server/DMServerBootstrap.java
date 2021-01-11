package dolphinmovie.server;

import java.net.InetSocketAddress;

import dolphinmovie.server.handler.SBChildHandlerInitializer;
import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.util.Security;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

public class DMServerBootstrap {
	private final EventLoopGroup group = new NioEventLoopGroup();
	private Channel channel;
	
	public ChannelFuture start(InetSocketAddress address) {
		ServerBootstrap b = new ServerBootstrap();
		try {
			b.group(group)
			 .channel(NioServerSocketChannel.class)
			 .childHandler(createInitializer());
		} catch (Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		}
		ChannelFuture f = b.bind(address);
		f.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					System.out.println("Server Start");
					LogManager.runningLog("Server Start");
				} else {
					System.err.println("Server failed");
					LogManager.runningLog("Server failed");
					LogManager.errorLog("Server failed");
				}
			}
			
		});
		f.syncUninterruptibly();
		channel = f.channel();
		return f;
	}
	
	public void destroy() {
		if(channel != null) {
			channel.close();
		}
		
		group.shutdownGracefully();
	}
	
	private ChannelInitializer<Channel> createInitializer() throws Exception {
		SslContext context = Security.getsslContext();
		return new SBChildHandlerInitializer(context);
	}
	
	
}
