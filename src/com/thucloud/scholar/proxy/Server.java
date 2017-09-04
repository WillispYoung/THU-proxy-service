package com.thucloud.scholar.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server{

	private static String proxyHost;
	private static int proxyPort;
	private static int listenPort;
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		Executor executor = Executors.newCachedThreadPool();
		
		initConfig();

		try {
			ServerSocket ss = new ServerSocket(listenPort);
			while(true) {
				final Socket s = ss.accept();
				onAccept(executor, s);
			}
		} catch (Exception e) {
			System.out.println("Error: Server stop because of " + e);
		}
	}

	public static void onAccept(Executor executor, final Socket s) throws SocketException, IOException {
		s.setSoTimeout(10000);
		try {
			final Socket s2 = new Socket(proxyHost, proxyPort);
			s2.setSoTimeout(10000);
			Runnable t1 = new Runnable() {
				@Override
				public void run() {
					InputStream in = null;
					OutputStream out = null;
					try {
						in = new ScholarInputStream(new BufferedInputStream(s.getInputStream()));
						out = s2.getOutputStream();
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
					InputStream in = null;
					OutputStream out = null;
					try {
						in = s2.getInputStream();
						out = new ScholarOutputStream(new BufferedOutputStream(s.getOutputStream()));
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


	private static void initConfig() {
		Config.loadConfig("servers.conf");
		proxyHost = Config.get("proxy-host", "127.0.0.1");
		proxyPort = Config.get("proxy-port", 3128);
		listenPort = Config.get("listen-port", 4129);

		System.out.println("Proxy: " + proxyHost + ":" + proxyPort);
		System.out.println("Listen: " + listenPort);
		System.out.println("Seed: " + Config.SEED);
	}

}
