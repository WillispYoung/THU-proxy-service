package proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.sound.sampled.Line;

public class Util {
	public static float getFlowResult(int portNum) throws IOException, ParseException {
		BufferedReader inFile = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proxy/flow/"+String.valueOf(portNum)+".flow"))));
		float rawResult;
		Date time = new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("MM-dd,HH:mm:ss");
		Stack<String> stack = new Stack<String>();
		String line = null;
		while((line = inFile.readLine()) != null) {
			stack.push(line);
		}

		// last line
		line = stack.pop();
		if (!line.contains(":")) {
			rawResult = Float.parseFloat(line);
		}else {	// wrong
			System.out.println("error: flow result" + portNum);
			inFile.close();
			return 0;
		}

		// last two line
		line = stack.pop();
		if (line.contains(":")) {	// correct
			time = formatter.parse(line);
		} else {	// wrong
			System.out.println("error: flow result" + portNum);
		}
		inFile.close();
		return rawResult/(1024*1024);
	}


	public static float getPreFlow(int portNum) throws IOException, ParseException {
		BufferedReader inFile = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proxy/flow/"+String.valueOf(portNum)+".flow"))));
		float rawResult;
		Date time = new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("MM-dd,HH:mm:ss");
		Stack<String> stack = new Stack<String>();
		String line = null;
		while((line = inFile.readLine()) != null) {
			stack.push(line);
		}
		inFile.close();

		// 先去掉前两行
		line = stack.pop();
		if (line != null) {
			line = stack.pop();
			if (line == null) {
				System.out.println("error: flow result" + portNum);
				return 0;
			}
		} else {
			System.out.println("error: flow result" + portNum);
			return 0;
		}


		// last line
		line = stack.pop();
		if (!line.contains(":")) {
			rawResult = Float.parseFloat(line);
		}else {	// wrong
			System.out.println("error: flow result" + portNum);
			return 0;
		}

		// last two line
		line = stack.pop();
		if (line.contains(":")) {	// correct
			time = formatter.parse(line);
		} else {	// wrong
			System.out.println("error: flow result" + portNum);
			return 0;
		}

		return rawResult/(1024*1024);
	}


	public static String getIPAdress(int portNum) throws IOException, ParseException {
		BufferedReader inFile = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proxy/ip/"+String.valueOf(portNum)+".ip"))));
		String IPAdress;
		Date time = new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
		Stack<String> stack = new Stack<String>();
		String line = null;
		while((line = inFile.readLine()) != null) {
			stack.push(line);
		}

		// last line
		line = stack.pop();
		if (!line.contains(":")) {
			IPAdress = line;
		}else {	// wrong
			System.out.println("error: IP adress" + portNum);
			return null;
		}

		// last two line
		line = stack.pop();
		if (line.contains(":")) {	// correct
			time = formatter.parse(line);
		} else {	// wrong
			System.out.println("error: flow result" + portNum);
		}
		inFile.close();

		try {
			Process process = null;
            process = Runtime.getRuntime().exec("python3 /home/zy/script/getcity.py "+IPAdress);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String l = input.readLine();
            l = l.substring(2, l.length()-1);
            System.out.println(l);
            l = l.replaceAll(".x", "%");
            System.out.println(l);
            IPAdress += "@"+l;
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

		return IPAdress;
	}

	public static String[] getIPAdressList(int portNum) throws IOException, ParseException {
		BufferedReader inFile = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proxy/ip/"+String.valueOf(portNum)+".ip"))));
		String[] IPInfo = new String[]{"","","","",""};
		Date time = null;
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
		SimpleDateFormat formatter1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Stack<String> stack = new Stack<String>();
		String line = null, ip = null;
		while((line = inFile.readLine()) != null) {
			stack.push(line);
		}

		// last line

		int count = 0;
		while (true) {
			if (count >= 5) {
				break;
			}
			try {
				line = stack.pop();
				if (!line.contains(":")) {
					IPInfo[count] = line;
					ip = line;
				}else {	// wrong
					System.out.println("error: IP adress" + portNum);
					return null;
				}

				// last two line
				line = stack.pop();
				if (line.contains(":")) {	// correct
					time = formatter.parse(line);
					IPInfo[count] += "@"+formatter1.format(time);
				} else {	// wrong
					System.out.println("error: flow result" + portNum);
					return null;
				}

				try {
					Process process = null;
					System.out.println(ip);
		            process = Runtime.getRuntime().exec("python3 /home/zy/script/getcity.py "+ip);
		            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            String l = input.readLine();
		            l = l.substring(2, l.length()-1);
		            System.out.println(l);
		            l = l.replaceAll(".x", "%");
		            System.out.println(l);
		            IPInfo[count++] += "@"+l;
		            input.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		            IPInfo[count++] += "@";
		        }
			} catch (EmptyStackException e) {
				// TODO: handle exception
				System.out.println(e);
				break;
			}
		}

		inFile.close();
		return IPInfo;
	}
}
