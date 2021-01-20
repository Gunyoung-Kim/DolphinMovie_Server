package dolphinmovie.server.handler;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.movieInfo.MovieDAO;
import dolphinmovie.server.navermovie.CurrentScreeningMovie;
import dolphinmovie.server.navermovie.NaverMovie;
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

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

	NaverMovie naverMovie;
	CurrentScreeningMovie screeningMovie;
	
	public HttpRequestHandler() {
		this.naverMovie = NaverMovie.getInstance();
		this.screeningMovie = CurrentScreeningMovie.getInstance();
	}
	
	@Override 
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + " has connected");
		LogManager.connectionLog(ctx.channel().remoteAddress() + " has connected");
	}
	
	@Override 
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + " has disconnected");
		LogManager.connectionLog(ctx.channel().remoteAddress() + " has disconnected");
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		if(msg.method().name().equals("GET") && msg.uri().equals("/rank")) {
			LinkedList<MovieDAO> dailyDAO = naverMovie.getDailyDAO();
			LinkedList<MovieDAO> weeklyDAO = naverMovie.getWeeklyDAO();
			LinkedList<MovieDAO> screeningDAO = screeningMovie.getCurrentScreeningMovies();
			
			//logging
			System.out.println(msg);
			Map<String,String> map = new LinkedHashMap<>();
			map.put("HOST", ctx.channel().remoteAddress().toString());
			map.put("Method", msg.method().name());
			map.put("User-Agent",msg.headers().get("User-Agent"));
			map.put("URI", msg.uri());
			LogManager.httpLog(map);
			
			//buildup data
			String result = proccessMovieInfo(dailyDAO,weeklyDAO,screeningDAO);
			
			//buildup httpresponse
			HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
			
			boolean keepAlive = HttpUtil.isKeepAlive(msg);
			if(keepAlive) {
				response.headers().set("Content-Length",result.length());
				response.headers().set("Connection", "Keep-Alive");
			}
			
			ctx.write(response);
			
			
			HttpContent content = new DefaultHttpContent(Unpooled.copiedBuffer(result,CharsetUtil.UTF_8));
			ctx.write(content);
			
			ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
	
			if(!keepAlive)
				f.addListener(ChannelFutureListener.CLOSE);
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	@Override 
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Read Complete");
		ctx.flush();
	}
	
	private String proccessMovieInfo(LinkedList<MovieDAO> daily, LinkedList<MovieDAO> weekly, LinkedList<MovieDAO> screening) {
		JSONObject json = new JSONObject();
	
		JSONArray dailyArr = new JSONArray();
		for(MovieDAO movie: daily) {
			JSONObject movie_json = movie.toJSONObject();
			dailyArr.add(movie_json);
		}
		
		JSONArray weeklyArr = new JSONArray();
		for(MovieDAO movie: weekly) {
			JSONObject movie_json = movie.toJSONObject();
			weeklyArr.add(movie_json);
		}
		
		JSONArray screeningArr = new JSONArray();
		for(MovieDAO movie: screening) {
			JSONObject movie_json = movie.toJSONObject();
			screeningArr.add(movie_json);
		}
		
		JSONObject result = new JSONObject();
		result.put("dailyMovies", dailyArr);
		result.put("weeklyMovies", weeklyArr);
		result.put("screeningMovies", screeningArr);
		json.put("boxofficeResult", result);
		
		return json.toJSONString();
	}
	
	@Override 
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogManager.errorLog(cause.getLocalizedMessage());
		ctx.close();
	}
	
}
