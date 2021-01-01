package dolphinmovie.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class CommandLineExecutor implements Runnable {
	
	private String[] commands;
	
	public CommandLineExecutor(String[] commands) {
		this.commands = commands;
	}

	@Override
	public void run() {
		Process process = null;
		Runtime runtime = Runtime.getRuntime();
		BufferedReader successReader = null;
		BufferedReader errorReader= null;
		StringBuilder successResult = new StringBuilder();
		StringBuilder failedResult = new StringBuilder();
		String msg;
		
		LinkedList<String> cmdList = new LinkedList<>();
		
		if(System.getProperty("os.name").indexOf("Windows") > -1) {
			cmdList.add("cmd");
			cmdList.add("/c");
		} else {
			cmdList.add("/bin/sh");
            cmdList.add("-c");
		}
		
		for(String str: commands) {
			cmdList.add(str);
		}
		
		String[] cmdArr = cmdList.toArray(new String[cmdList.size()]);
		
		try {
			process = runtime.exec(cmdArr);
			
			successReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"EUC-KR"));
			
			while((msg = successReader.readLine()) != null) {
				successResult.append(msg + System.getProperty("line.separator"));
			}
			
			errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),"EUC-KR"));
			
			while((msg = errorReader.readLine()) != null) {
				failedResult.append(msg + System.getProperty("line.separator"));
			}
			
			process.waitFor();
			
			if(process.exitValue() == 0) {
				System.out.println("성공!");
				System.out.println(successResult.toString());
			} else {
				System.out.println("비정상 종료!");
				System.out.println(successResult.toString());
			}
			
			if (failedResult.length() != 0) {
                System.out.println("오류");
                System.out.println(failedResult.toString());
            }
		
		} catch(IOException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
                if(process != null) process.destroy();
                if (successReader != null) successReader.close();
                if (errorReader != null) errorReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
		}
		
	}
	
}
