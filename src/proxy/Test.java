package proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Test {

	public static void main(String[] args) throws IOException {
		String ip = args[0];
		System.out.println(ip);
		Process process = Runtime.getRuntime().exec("python3 /root/THU-proxy-service/scripts/get_ip_location.py " + ip);
       	BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String l = input.readLine();
        input.close();
	}
}
