/**
 * The class of synchronized counter which helps keep track of the number of connections.
 *
 */
public class SynchronizedCounter {
	private int counter;

	/**
	 * Initialize counter with initial value.
	 * @param counter - Initial number
	 */
	public SynchronizedCounter(int counter) {
		this.counter = counter;
	}

	/**
	 * Initialize counter with initial value 0.
	 */
	public SynchronizedCounter() {
		this(0);
	}

	/**
	 * Update the counter when new connection is established.
	 */
	public synchronized void increment() {
		counter = counter + 1;
	}

	/**
	 * Update the counter when a connection is terminated.
	 */
	public synchronized void decrement() {
		counter = counter - 1;
	}

	/**
	 * Retrieve current value of counter
	 * @return current value of counter
	 */
	public int getCounter() {
		return this.counter;
	}

	@Override
	public String toString() {
		return "" + counter;
	}
}
