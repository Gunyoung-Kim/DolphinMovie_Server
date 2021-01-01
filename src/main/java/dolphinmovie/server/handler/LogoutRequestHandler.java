package dolphinmovie.server.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.user.UserDAO;
import dolphinmovie.server.user.UserManager;
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

public class LogoutRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

	UserManager user = UserManager.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		if(msg.method().name().equals("POST") && msg.uri().equals("/logout")) {
			
			System.out.println(msg);
			Map<String,String> map = new LinkedHashMap<>();
			map.put("HOST", ctx.channel().remoteAddress().toString());
			map.put("Method", msg.method().name());
			map.put("User-Agent",msg.headers().get("User-Agent"));
			map.put("URI", msg.uri());
			LogManager.httpLog(map);
			
			HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
			
			boolean isKeepAlive = HttpUtil.isKeepAlive(msg);
			if(isKeepAlive) {
				response.headers().set("Connection", "Keep-Alive");
			}
			ctx.write(response);
			
			String requestBody = msg.content().toString(CharsetUtil.UTF_8);
			JSONParser parser = new JSONParser();
			
			JSONObject requestJSON = (JSONObject) parser.parse(requestBody); 
			
			String logoutId = (String) requestJSON.get("id");
			
			UserDAO logoutResult = user.logout(logoutId);
			
			JSONObject responseJSON = new JSONObject();
			
			if(logoutResult == null) {
				responseJSON.put("result_code", 0);
				responseJSON.put("result", "FAIL");
				responseJSON.put("error_msg", "fail");
			} else {
				responseJSON.put("result_code", 0);
				responseJSON.put("result", "SUCCESS");
				responseJSON.put("error_msg", "정상적으로 처리되었습니다.");
			}
			
			HttpContent content = new DefaultHttpContent(Unpooled.copiedBuffer(responseJSON.toJSONString(), CharsetUtil.UTF_8));
			ctx.write(content);
			
			ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

			if(!isKeepAlive) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
			
		} else {
			ctx.fireChannelRead(msg);		
		}
		
	}
	
	@Override 
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogManager.errorLog(cause.getLocalizedMessage());
		ctx.close();
	}

}
