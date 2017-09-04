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
		Client.initConfig();
		Executor executor = Executors.newCachedThreadPool();
		Client s = new Client(3128);
		executor.execute(s);
	}
}
