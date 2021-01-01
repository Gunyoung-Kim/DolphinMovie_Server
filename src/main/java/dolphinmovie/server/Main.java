package dolphinmovie.server;

import java.awt.EventQueue;
import java.awt.Frame;
import java.net.InetSocketAddress;

import javax.swing.WindowConstants;

import dolphinmovie.server.theater.TheaterManager;
import io.netty.channel.ChannelFuture;

public class Main {

	private static final int DEFAULT_PORT = 8888;
	
	public static void main(String[] args) {
		TheaterManager theaterManager = TheaterManager.getInstance();
		theaterManager.createTable();
		
		DMServerMainUI ui = new DMServerMainUI();
		ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ui.pack();
		EventQueue.invokeLater(new FrameShower(ui));
		
		int port;
		final DMServerBootstrap server = new DMServerBootstrap();
		
		if(args.length == 0) {
			port = DEFAULT_PORT;
		} else {
			port = Integer.parseInt(args[0]);
		}
		
		ChannelFuture f = server.start(new InetSocketAddress(port));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override 
			public void run() {
				server.destroy();
			}
		});
		
		f.channel().closeFuture().syncUninterruptibly();
		
		
	}
	
	private static class FrameShower implements Runnable {
		
		private final Frame frame;
		
		FrameShower(Frame frame) {
			this.frame = frame;
		}
		
		@Override 
		public void run() {
			frame.setVisible(true);
		}
	}

}
