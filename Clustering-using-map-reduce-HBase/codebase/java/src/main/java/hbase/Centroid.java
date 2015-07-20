package hbase;

public class Centroid {

	private String key;
	private int sequence;
	private String Centroid;
	
	public Centroid(String key, int sequence, String centroid) {
		super();
		this.key = key;
		this.sequence = sequence;
		Centroid = centroid;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the centroid
	 */
	public String getCentroid() {
		return Centroid;
	}

	/**
	 * @param centroid
	 *            the centroid to set
	 */
	public void setCentroid(String centroid) {
		Centroid = centroid;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

}
