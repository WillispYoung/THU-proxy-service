package proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class ScholarOutputStream extends OutputStream {

	static final byte[] MAP = new byte[256];
	static final byte[] MASK = new byte[256];

	public static void genMap() {
		Random rand = new Random(Config.SEED);
		for(int i=0; i<MAP.length; i++) {
			MAP[i] = (byte)i;
		}
		for(int i=0; i<MAP.length; i++) {
			int k = rand.nextInt(MAP.length - i) + i;
			byte t = MAP[i];
			MAP[i] = MAP[k];
			MAP[k] = t;
		}
		for(int i=0; i<MASK.length; i++) {
			MASK[i] = (byte)rand.nextInt(256);
		}
	}
	
	private OutputStream parent;
	private int pos = 0;

	public ScholarOutputStream(OutputStream parent) {
		this.parent = parent;
	}

	@Override
	public void write(int b) throws IOException {
		parent.write((MAP[b & 0xFF] ^ MASK[pos]) & 0xFF);
		pos = (pos + 1) % MASK.length;
	}
	

	@Override
	public void flush() throws IOException {
		parent.flush();
	}
	
}
