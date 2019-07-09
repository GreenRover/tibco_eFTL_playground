package ch.sbb.ftl.demo.helper;

public class MessageConstants {

	public static int SENDING_COUNT = 100_000;
	public static int BATCH_COUNT = 1;
	public static int PARALLEL_THREADS = 12;

	public static final int REQUEST_TIMEOUT_IN_MILLIS = 10_000;

	public static final int MAX_MESSAGES_IN_QUEUE = 1_000;

	public enum DataType {
		MESSAGE_B10, MESSAGE_B20, MESSAGE_B50, MESSAGE_B100, MESSAGE_B200, MESSAGE_B500, //
		MESSAGE_K1, MESSAGE_K2, MESSAGE_K5, MESSAGE_K10, MESSAGE_K100, MESSAGE_K1000
	}

	public static String MESSAGE_B10 = createStringOfSize(10);
	public static String MESSAGE_B20 = createStringOfSize(20);
	public static String MESSAGE_B50 = createStringOfSize(20);
	public static String MESSAGE_B100 = createStringOfSize(100);
	public static String MESSAGE_B200 = createStringOfSize(200);
	public static String MESSAGE_B500 = createStringOfSize(500);

	public static String MESSAGE_K1 = createStringOfSize(1000);
	public static String MESSAGE_K2 = createStringOfSize(2000);
	public static String MESSAGE_K5 = createStringOfSize(5000);
	public static String MESSAGE_K10 = createStringOfSize(10_000);

	public static String MESSAGE_K100 = createStringOfSize(100_000);
	public static String MESSAGE_K1000 = createStringOfSize(1_000_000);

	public static String createStringOfSize(int n) {
		StringBuilder outputBuffer = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			outputBuffer.append("x");
		}
		return outputBuffer.toString();
	}

}
