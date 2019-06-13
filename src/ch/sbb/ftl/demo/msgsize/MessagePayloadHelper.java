package ch.sbb.ftl.demo.msgsize;

import java.util.Objects;

public class MessagePayloadHelper {

	public static void processPayload(String payload, int count)  {
		
		final String countInfo = calcCountInfo(count);
		final int msgLength = payload.length();

		System.out.printf("Elapse Time in ms: %5s %s TextMessage received from Topic: %s | %d bytes %n", //
				getTimeWhenSent(payload), //
				countInfo, //
				extractMessageInfo(payload), //
				msgLength);
	}

	private static String getTimeWhenSent(String text) {
		long elapseTime = System.currentTimeMillis() - Long.parseLong(text.split(";")[0]);
		return String.valueOf(elapseTime);
	}

	private static String extractMessageInfo(String text) {
		String s = text.split(";")[1];
		if (Objects.isNull(s)) {
			return "-";
		} else if (s.length() > 40) {
			return s.substring(0, 40);
		} else {
			return s;
		}
	}

	private static String calcCountInfo(int count) {
		return String.format(" [%d of %d] ", count, MessageConstants.SENDING_COUNT);
	}
	

	public static String createPayload(final MessageConstants.DataType dataType, final int i, String info) {
		StringBuilder sb = new StringBuilder();
		sb.append(System.currentTimeMillis() + ";");
		sb.append(info).append(" | ");
		switch (dataType) {
		case K1_TextMessage:
			sb.append(String.format("%d %s!", i, MessageConstants.MESSAGE_K1));
			break;
		case K10_TextMessage:
			sb.append(String.format("%d %s!", i, MessageConstants.MESSAGE_K10));
			break;
		case K100_TextMessage:
			sb.append(String.format("%d %s!", i, MessageConstants.MESSAGE_K100));
			break;
		case K1000_TextMessage:
			sb.append(String.format("%d %s!", i, MessageConstants.MESSAGE_K1000));
			break;
		default:
			throw new RuntimeException("Unexpected DataType");
		}
		return sb.toString();	
	}
}
