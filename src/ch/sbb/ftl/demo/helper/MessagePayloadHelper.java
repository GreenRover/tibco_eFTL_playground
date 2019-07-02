package ch.sbb.ftl.demo.helper;

import java.util.Objects;

public class MessagePayloadHelper {

	/**
	 * Extract information from the payload. The payload has the structure:
	 * <li>time when message is created by the publisher
	 * <li>information (e.g. topic name)
	 * <li>message id (number starting at 1)
	 * <li>data (lots of x)
	 * <li>example:
	 * 
	 * <pre>
	 * 1560490790836;msgsize/direct/json/myclass/1.0 | 1 xx
	 * </pre>
	 * 
	 * @param payload
	 * @param count
	 */
	public static void processPayload(final String payload, final int count) {
		final String countInfo = calcCountInfo(count);
		final int msgLength = payload.length();

		System.out.printf("Elapse Time in ms: %5s %s TextMessage received from Topic: %s | %d bytes %n", //
				getTimeWhenSent(payload), //
				countInfo, //
				extractMessageInfo(payload), //
				msgLength);
	}

	private static String getTimeWhenSent(final String text) {
		final long elapseTime = System.currentTimeMillis() - Long.parseLong(text.split(";")[0]);
		return String.valueOf(elapseTime);
	}

	private static String extractMessageInfo(final String text) {
		final String s = text.split(";")[1];
		if (Objects.isNull(s)) {
			return "-";
		} else if (s.length() > 40) {
			return s.substring(0, 40);
		} else {
			return s;
		}
	}

	private static String calcCountInfo(final int count) {
		return String.format(" [%d of %d] ", count, MessageConstants.SENDING_COUNT);
	}

	public static String createPayload(final String text, final int i, final String info) {
		final StringBuilder sb = new StringBuilder();
		sb.append(System.currentTimeMillis() + ";");
		sb.append(info).append(" | ");
		sb.append(String.format("%d %s!", i, text));
		return sb.toString();
	}

	public static String createPayload(final MessageConstants.DataType dataType, final int i, final String info) {
		return createPayload(dataType.toString(), i, info);
	}

}
