package dolphinmovie.server.handler;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.theater.TheaterDAO;
import dolphinmovie.server.theater.TheaterManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class TheaterRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	TheaterManager theater;
	
	public TheaterRequestHandler() {
		this.theater = TheaterManager.getInstance();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		if(msg.method().name().equals("GET") && msg.uri().equals("/theater")) {
			LinkedList<TheaterDAO> list = theater.getTheaterList();
			
			System.out.println(msg);
			Map<String,String> map = new LinkedHashMap<>();
			map.put("HOST", ctx.channel().remoteAddress().toString());
			map.put("Method", msg.method().name());
			map.put("User-Agent",msg.headers().get("User-Agent"));
			map.put("URI", msg.uri());
			LogManager.httpLog(map);
			
			String result = processTheaterInfo(list);
			
			//buildup httpresponse
			HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
			
			boolean keepAlive = HttpUtil.isKeepAlive(msg);
			if(keepAlive) {
				response.headers().set("Contect-Length",result.length());
				response.headers().set("Connection", "Keep-Alive");
			}
			
			ctx.write(response);
			
			HttpContent content = new DefaultHttpContent(Unpooled.copiedBuffer(result,CharsetUtil.UTF_8));
			ctx.write(content);
			
			ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
			
			if(!keepAlive)
				f.addListener(ChannelFutureListener.CLOSE);
		} else {
			System.out.println(msg.method().name() +" : "+ msg.uri());
			ctx.fireChannelRead(msg);
		}
	}
	
	private String processTheaterInfo(LinkedList<TheaterDAO> list) {
		JSONObject json = new JSONObject();
		JSONObject resultJSON = new JSONObject();
		JSONArray theaters = new JSONArray();
		
		for(TheaterDAO t : list) {
			JSONObject o = t.toJSONObject();
			
			theaters.add(o);
		}
		
		resultJSON.put("theaters", theaters);
		json.put("theaterResult", resultJSON);
		
		String result = json.toJSONString();
		
		return result;
	}

	@Override 
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogManager.errorLog(cause.getLocalizedMessage());
		ctx.close();
	}
}
