package proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.transform.Templates;
import javax.xml.ws.handler.MessageContext;

public class ProxyManager  {
	private static int listenPort = 4127;
	public static Map<Integer, Integer> bandwidth = new HashMap<Integer, Integer>();

    @SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
    	bandwidth.put(1, 1);
    	bandwidth.put(5, 2);
    	bandwidth.put(10, 5);
    	bandwidth.put(20, 10);
    	bandwidth.put(50, 20);
    	ServerSocket ss = null;
		try {
			ss = new ServerSocket(listenPort);
			Executor executor = Executors.newCachedThreadPool();
			Client.initConfig();

	        System.out.println("manager startup.");
	        while (true) {
	            try {
	                Socket s = ss.accept();
	                new Handler(s, executor).start();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }

}

class Handler extends Thread {
    Socket socket;
    Executor executor;

    public Handler(Socket s, Executor executor) {
        this.socket = s;
        this.executor = executor;
    }

    @Override
    public void run() {
        System.out.println("in handling..");

        try {
            InputStream is = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String data = in.readLine();
            System.out.println("read line " + " :" + data);
            String head = null, msg = null;
            head = data.split("@")[0];
            msg = data.split("@")[1];

            if (head.equals("addport")) {
            	int portNum = 0, type = 0;
            	portNum = Integer.parseInt(msg.split(",")[0]);
            	type = (int)Float.parseFloat(msg.split(",")[1]);

            	Client s = new Client(portNum);
				executor.execute(s);
				System.out.println("open port"+portNum+"type"+type);
				if (type != 0) {
					Runtime.getRuntime().exec("iptables -A OUTPUT -p tcp --sport "+portNum+" -j ACCEPT");
					Runtime.getRuntime().exec("iptables -t mangle -A OUTPUT -p tcp --sport "+portNum+" -j MARK --set-mark "+(portNum-10000));
					Runtime.getRuntime().exec("tc class add dev eth9 parent  1: classid 1:"+(portNum-10000)+" htb rate "+ProxyManager.bandwidth.get(type)+"mbit ceil "+(ProxyManager.bandwidth.get(type)+1)+"mbit burst 20k");
					Runtime.getRuntime().exec("tc filter add dev eth9 parent 1: protocol ip prio 1 handle "+(portNum-10000)+" fw classid 1:"+(portNum-10000));
				}
			} else if (head.equals("upgrade")) {
				int portNum = 0, type = 0;
            	portNum = Integer.parseInt(msg.split(",")[0]);
            	type = (int)Float.parseFloat(msg.split(",")[1]);

            	if (type != 0) {
            		Runtime.getRuntime().exec("tc class change dev eth9 parent  1: classid 1:"+(portNum-10000)+" htb rate "+ProxyManager.bandwidth.get(type)+"mbit ceil "+(ProxyManager.bandwidth.get(type)+1)+"mbit burst 20k");
				}
				Runtime.getRuntime().exec("iptables -D INPUT -p tcp --dport "+portNum+" -j DROP");
			} else if (head.equals("downgrade")) {
				int portNum = 0, type = 0;
            	portNum = Integer.parseInt(msg.split(",")[0]);
            	type = (int)Float.parseFloat(msg.split(",")[1]);

            	if (type != 0) {
            		Runtime.getRuntime().exec("tc class change dev eth9 parent  1: classid 1:"+(portNum-10000)+" htb rate "+ProxyManager.bandwidth.get(type)+"mbit ceil "+(ProxyManager.bandwidth.get(type)+1)+"mbit burst 20k");
				}
			} else if (head.equals("reopen")) {
				int portNum = 0;
				portNum = Integer.parseInt(msg);
				System.out.println("reopen port"+portNum);

				Runtime.getRuntime().exec("iptables -D INPUT -p tcp --dport "+portNum+" -j DROP");
				Runtime.getRuntime().exec("iptables -D INPUT -p tcp --dport "+portNum+" -j DROP");
				Runtime.getRuntime().exec("iptables -D INPUT -p tcp --dport "+portNum+" -j DROP");

				File file = new File("/proxy/over_flow/"+portNum+".1");
				if (file.isFile() && file.exists()) {
					file.delete();
					System.out.println("1");
				}
				file = new File("/proxy/over_flow/"+portNum+".2");
				if (file.isFile() && file.exists()) {
					file.delete();
					System.out.println("2");
				}

			} else if (head.equals("close")) {
				int portNum = 0, type;
				portNum = Integer.parseInt(msg.split(",")[0]);
            	type = Integer.parseInt(msg.split(",")[1]);
            	System.out.println("close port"+portNum+"type"+type);

				Runtime.getRuntime().exec("iptables -A INPUT -p tcp --dport "+portNum+" -j DROP");
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("/proxy/over_flow/"+String.valueOf(portNum)+"."+String.valueOf(type)))));
				out.write(" ");
				out.close();
			} else if (head.equals("getflow")) {
				int portNum = 0;
				portNum = Integer.parseInt(msg);

				BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				float flowResult;
				try {
					flowResult = Util.getFlowResult(portNum);
				} catch (Exception e) {
					flowResult = 0;
				}

				outStream.write(String.valueOf(flowResult));
				System.out.println("get "+portNum+" flow result "+ flowResult);;
				outStream.flush();
			} else if (head.equals("preflow")) {
				int portNum = 0;
				portNum = Integer.parseInt(msg);

				BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				float flowResult;
				try {
					flowResult = Util.getPreFlow(portNum);
				} catch (Exception e) {
					flowResult = 0;
				}

				outStream.write(String.valueOf(flowResult));
				System.out.println("get "+portNum+" pre flow "+ flowResult);;
				outStream.flush();
			} else if (head.equals("getIP")) {
				int portNum = 0;
				portNum = Integer.parseInt(msg);

				BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				String IPAddress;
				try {
					IPAddress = Util.getIPAdress(portNum);
				} catch (Exception e) {
					IPAddress = "@";
				}
				outStream.write(IPAddress);
				System.out.println("get "+portNum+" IP "+ IPAddress);;
				outStream.flush();
			}
			else if (head.equals("getIPList")) {
				int portNum = 0;
				portNum = Integer.parseInt(msg);

				BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				String[] IPInfo;
				try {
					IPInfo = Util.getIPAdressList(portNum);
				} catch (Exception e) {
					IPInfo = null;
					outStream.write("");
				}

				if (IPInfo != null) {
					for (int i = 0; i < IPInfo.length; i++) {
						if (IPInfo[i].equals("")) break;
						System.out.println(IPInfo[i]);
						outStream.write(IPInfo[i]+",");
					}
				}

				System.out.println("get "+portNum+" IP list");
				outStream.flush();
			}
			else {
				System.err.println("error message");
			}
			socket.close();
            System.out.println("done.");
        } catch (Exception e) {
        	System.out.println(e);
        }
    }
}
