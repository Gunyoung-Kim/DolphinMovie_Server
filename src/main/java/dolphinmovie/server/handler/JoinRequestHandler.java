package dolphinmovie.server.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.member.MemberManager;
import io.netty.buffer.ByteBuf;
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

public class JoinRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

	MemberManager memberManager = MemberManager.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		if(msg.method().name().equals("POST") && msg.uri().equals("/join")) {
			
			//logging
			System.out.println(msg);
			Map<String,String> map = new LinkedHashMap<>();
			map.put("HOST", ctx.channel().remoteAddress().toString());
			map.put("Method", msg.method().name());
			map.put("User-Agent",msg.headers().get("User-Agent"));
			map.put("URI", msg.uri());
			LogManager.httpLog(map);
			
			//data processing
			ByteBuf httpBody = msg.content();
			String bodyString = httpBody.toString(CharsetUtil.UTF_8);
			
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(bodyString);
			
			String id = (String) object.get("id");
			String password = (String) object.get("password");
			String name = (String) object.get("name");
			
			//joining
			int joinResult = memberManager.addMember(id, password, name);
			
			HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
			
			boolean isKeepAlive = HttpUtil.isKeepAlive(msg);
			if(isKeepAlive) {
				response.headers().set("Connection", "Keep-Alive");
			}
			ctx.write(response);
			JSONObject responseJSON = new JSONObject();
			
			if(joinResult == 0) {
				responseJSON.put("result_code", 0);
				responseJSON.put("result", "SUCCESS");
				JSONObject user_info = new JSONObject();
				user_info.put("id", id);
				user_info.put("password", password);
				user_info.put("name", name);
				responseJSON.put("error_msg", "welcome!");
				responseJSON.put("user_info",user_info);
			} else {
				responseJSON.put("result_code", 507);
				responseJSON.put("result", "FAIL");
				JSONObject user_info = new JSONObject();
				user_info.put("id", "");
				user_info.put("password", "");
				user_info.put("name", "");
				responseJSON.put("error_msg", "Insufficient Storage");
				responseJSON.put("user_info",user_info);
			}
			

			HttpContent content = new DefaultHttpContent(Unpooled.copiedBuffer(responseJSON.toJSONString(),CharsetUtil.UTF_8));
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
