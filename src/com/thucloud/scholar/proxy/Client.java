package com.thucloud.scholar.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Client implements Runnable {

	private static String serverHost;
	private static int serverPort;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
	
	private final int portNum;
	
	private InetAddress ipAddr;
	private Date loginTime;
	
	public Client(int portNum) throws UnknownHostException {
		this.portNum = portNum;
		this.ipAddr = InetAddress.getLocalHost();
		this.loginTime = new Date();
	}
	
	@Override
	public void run() {
		Executor executor = Executors.newCachedThreadPool();
		System.out.println("listen:"+this.portNum);
		
		try {
			ServerSocket ss = new ServerSocket(portNum);
			int count = 0;
			while(true) {
				final Socket sock = ss.accept();
				if (count++ % 100 == 0) {
					count = 0;
					if (! this.ipAddr.getHostAddress().equals(sock.getInetAddress().getHostAddress())) {
						this.loginTime = new Date();
						String loginTimeStr = this.dateFormat.format(this.loginTime);
						this.ipAddr = sock.getInetAddress();
						System.out.println("address of port "+portNum+" change to "+ipAddr+" at "+loginTime);
						Runtime.getRuntime().exec("/home/zy/script/record_ip.sh "+portNum+" "+ipAddr.getHostAddress()+" "+loginTimeStr);
					}
				}
				onAccept(executor, sock);
			}
		} catch (Exception e) {
			System.err.println("Error: listenPort stop because of " + e);
		}
		System.out.println("stoprun");
	}

	
	private static AtomicInteger threadCount = new AtomicInteger(0);
	
	public static void onAccept(Executor executor, final Socket s) throws SocketException, IOException {
		s.setSoTimeout(10000);
		try {
			final Socket s2 = new Socket(serverHost, serverPort);
			s2.setSoTimeout(10000);
			Runnable t1 = new Runnable() {
				@Override
				public void run() {
					// System.out.println("Thread count: " + threadCount.incrementAndGet());
					InputStream in = null;
					OutputStream out = null;
					try {
						in = new ScholarInputStream(new BufferedInputStream(s2.getInputStream()));
						out = s.getOutputStream();
						byte[] buffer = new byte[4096];
						int n = 1;
						while(n >= 0) {
							n = read(in, buffer);
							if(n > 0) {
								out.write(buffer, 0, n);
								out.flush();
							} else if(n == 0) {
								continue;
							}
						}
					} catch (Exception e) {
					} finally {
						// System.out.println("Thread count: " + threadCount.decrementAndGet());
						try { in.close(); } catch (Exception e) {}
						try { out.close(); } catch (Exception e) {}
						try { s.close(); } catch (Exception e) {}
						try { s2.close(); } catch (Exception e) {}
					}
				}
			};
			Runnable t2 = new Runnable() {
				@Override
				public void run() {
					// System.out.println("Thread count: " + threadCount.incrementAndGet());
					InputStream in = null;
					OutputStream out = null;
					try {
						in = s.getInputStream();
						out = new ScholarOutputStream(new BufferedOutputStream(s2.getOutputStream()));
						byte[] buffer = new byte[4096];
						int n = 1;
						while(n >= 0) {
							n = read(in, buffer);
							if(n > 0) {
								out.write(buffer, 0, n);
								out.flush();
							} else if(n == 0) {
								continue;
							}
						}
					} catch (Exception e) {
					} finally {
						// System.out.println("Thread count: " + threadCount.decrementAndGet());
						try { in.close(); } catch (Exception e) {}
						try { out.close(); } catch (Exception e) {}
						try { s.close(); } catch (Exception e) {}
						try { s2.close(); } catch (Exception e) {}
					}
				}
			};
			executor.execute(t1);
			executor.execute(t2);
		} catch(Exception e) {
			s.close();
		}
	}


	protected static int read(InputStream in, byte[] buffer) throws IOException {
		try {
			return in.read(buffer);
		} catch(SocketTimeoutException e) {
			return 0;
		}
	}

	public static void initConfig() {
		Config.loadConfig("servers.conf");
		serverHost = Config.get("server-host", "127.0.0.1");
		serverPort = Config.get("server-port", 4129);
		
		System.out.println("Server: " + serverHost + ":" + serverPort);
		System.out.println("Seed: " + Config.SEED);
	}
}
