package ch.sbb.ftl.demo.msgsize;

public class MessageConstants {

	public static int SENDING_COUNT = 204;

	public enum DataType {
		K1_TextMessage, K10_TextMessage, K100_TextMessage, K1000_TextMessage
	}

	public static String MESSAGE_K1 = createStringOfSize(1000);
	public static String MESSAGE_K10 = createStringOfSize(10_000);
	public static String MESSAGE_K100 = createStringOfSize(100_000);
	public static String MESSAGE_K1000 = createStringOfSize(1_000_000);

	private static String createStringOfSize(int n) {
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			sb.append("x");
		}
		return sb.toString();
	}
}
