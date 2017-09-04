package proxy;

import java.io.IOException;
import java.io.InputStream;

public class ScholarInputStream extends InputStream {

	static final byte[] MAP = new byte[256];
	static final byte[] MASK = new byte[ScholarOutputStream.MASK.length];

	public static void genMap() {
		byte[] from = ScholarOutputStream.MAP;
		for(int i=0; i<MAP.length; i++) {
			MAP[from[i] & 0xFF] = (byte)i;
		}
		System.arraycopy(ScholarOutputStream.MASK, 0, MASK, 0, MASK.length);
	}

	private InputStream parent;
	private int pos = 0;

	public ScholarInputStream(InputStream parent) {
		this.parent = parent;
	}

	@Override
	public int read() throws IOException {
		int n = parent.read();
		int r = n >= 0 ? (MAP[(n ^ MASK[pos]) & 0xFF]) & 0xFF : n;
		pos = (pos + 1) % MASK.length;
		return r;
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
		    throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
		    throw new IndexOutOfBoundsException();
		} else if (len == 0) {
		    return 0;
		}

		int c = read();
		if (c == -1) {
		    return -1;
		}
		b[off] = (byte)c;

		len = Math.min(len, parent.available());

		int i = 1;
		try {
		    for (; i < len ; i++) {
			c = read();
			if (c == -1) {
			    break;
			}
			b[off + i] = (byte)c;
		    }
		} catch (IOException ee) {
		}
		return i;
	}

}
