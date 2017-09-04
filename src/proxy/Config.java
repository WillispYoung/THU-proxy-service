package proxy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	public static long SEED;

	private static Properties p = new Properties();

	public static void loadConfig(String file) {
		InputStream in;
		
		try {
			in = new FileInputStream(file);
			p.load(in);
			SEED = Long.parseLong(get("seed", "0"));
			ScholarOutputStream.genMap();
			ScholarInputStream.genMap();
		} catch(Exception e) {
			
		}
	}
	
	public static int get(String s, int defaultValue) {
		String r = p.getProperty(s, null);
		if(r == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(r);
		} catch(Exception e) {
			return defaultValue;
		}
	}

	public static String get(String s, String defaultValue) {
		return p.getProperty(s, defaultValue);
	}

}
