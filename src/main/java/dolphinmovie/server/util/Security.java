package dolphinmovie.server.util;

import java.io.File;

import javax.net.ssl.SSLException;

import dolphinmovie.server.log.LogManager;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public class Security {
	public static SslContext getsslContext() throws SSLException {
		try {
			//인증서 가져오기
			File certi  = new File("/Users/kimgun-yeong/kimNetty.crt");
			//개인 키 가져오기
			File privateKey = new File("/Users/kimgun-yeong/privateKey.pem");
			
			return SslContextBuilder.forServer(certi, privateKey).build();
		} catch (NullPointerException ex) {
			LogManager.errorLog("Certification or Key path is null");
			return null;
		}
		
	}
}
