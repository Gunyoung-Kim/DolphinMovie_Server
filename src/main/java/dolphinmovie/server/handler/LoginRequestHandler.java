package dolphinmovie.server.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.member.MemberDAO;
import dolphinmovie.server.member.MemberManager;
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

public class LoginRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	MemberManager member = MemberManager.getInstance();
	UserManager user = UserManager.getInstance();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		if(msg.method().name().equals("POST") && msg.uri().equals("/login")) {
			
			//logging
			System.out.println(msg);
			Map<String,String> map = new LinkedHashMap<>();
			map.put("HOST", ctx.channel().remoteAddress().toString());
			map.put("Method", msg.method().name());
			map.put("User-Agent",msg.headers().get("User-Agent"));
			map.put("URI", msg.uri());
			LogManager.httpLog(map);
			
			//data processing
			String request_body = msg.content().toString(CharsetUtil.UTF_8);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request_body);
			
			String enteredId = (String) json.get("id");
			String enteredPw = (String) json.get("password");
			
			MemberDAO login_result = member.login(enteredId, enteredPw);
			
			HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
			
			boolean isKeepAlive = HttpUtil.isKeepAlive(msg);
			if(isKeepAlive) {
				response.headers().set("Connection", "Keep-Alive");
			}
			ctx.write(response);
			
			JSONObject loginResponse = new JSONObject();
			
			if(login_result == null) {
				loginResponse.put("result code", 200);
				loginResponse.put("result", "error");
				loginResponse.put("error_msg", "로그인 도중 오류가 발생했습니다.");
				
			} else if(login_result.getId().equals("NO_ACCOUNT")){
				loginResponse.put("result_code", 200);
				loginResponse.put("result", "NO_ACCOUNT");
				loginResponse.put("error_msg", "존재하지 않는 사용자입니다.");
			} else if(login_result.getId().equals("INCORRECTPW")) {
				loginResponse.put("result_code", 200);
				loginResponse.put("result", "INCORRECTPW");
				loginResponse.put("error_msg", "잘못된 비밀번호입니다.");
			} else {
				JSONObject memberjson = new JSONObject();
				memberjson.put("name", login_result.getName());
				memberjson.put("id", login_result.getId());
				loginResponse.put("user_info", memberjson);
				loginResponse.put("result_code", 0);
				loginResponse.put("result", "SUCCESS");
				loginResponse.put("error_msg", "정상적으로 처리되었습니다.");
				user.login(login_result);
			}
			
			HttpContent content = new DefaultHttpContent(Unpooled.copiedBuffer(loginResponse.toJSONString(),CharsetUtil.UTF_8));
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
