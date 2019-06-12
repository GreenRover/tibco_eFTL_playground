package ch.sbb.ftl.demo.msgsize;

import java.util.logging.Level;

import com.tibco.ftl.FTLException;
import com.tibco.ftl.Message;
import com.tibco.ftl.Publisher;
import com.tibco.ftl.Realm;

public class TopicPublisher {

	private static int messageId = 1;

	public static void main(final String... args) throws FTLException {
		FtlHelper.setupLogging(Level.WARNING);
		final Realm realm = FtlHelper.getRealm();
		System.out.println("TopicPublisher initializing...");

		final Publisher pub = realm.createPublisher(FtlHelper.ftlEndPoint);
		for (int i = 0; i < MessageConstants.SENDING_COUNT; i++) {
			runWithNewSession(i, pub, realm, FtlHelper.typeName, MessageConstants.DataType.K100_TextMessage);
		}
		System.out.println("DONE");
	}

	private static void runWithNewSession(final int i, final Publisher pub, final Realm realm, final String typeName, final MessageConstants.DataType dataType) {
		try {
			final Message msg = createMessage(realm, dataType, messageId, typeName);
			pub.send(msg);
			msg.destroy();
			System.out.println(calcCountInfo(i) + "MessageId-" + messageId + " sent");
			messageId++;
		} catch (final Exception e) {
			System.out.println(e);
		}
	}

	private static Message createMessage(final Realm realm, final MessageConstants.DataType dataType, final int id, final String typeName) throws FTLException {
		switch (dataType) {
		case K1_TextMessage:
			return createTextMessage(realm, id, 1000, typeName);
		case K10_TextMessage:
			return createTextMessage(realm, id, 10_000, typeName);
		case K100_TextMessage:
			return createTextMessage(realm, id, 100_000, typeName);
		case M1_TextMessage:
			return createTextMessage(realm, id, 1_000_000, typeName);
		default:
			throw new RuntimeException("Unexpected DataType");
		}
	}

	private static Message createTextMessage(final Realm realm, final int i, final int numOfChars, final String typeName) throws FTLException {
		final String text = String.format("%d %s!", i, createStringOfSize(numOfChars));
		final Message msg = realm.createMessage("lastest"); // Using dynamic schema
		msg.setString("type", typeName);
		msg.setString("payload", text);
		msg.setLong("number", Long.valueOf(i));
		return msg;
	}

	private static String createStringOfSize(final int n) {
		final StringBuilder outputBuffer = new StringBuilder(n);
		for (int i = 0; i < n; i++) {
			outputBuffer.append("x");
		}
		return outputBuffer.toString();
	}

	private static String calcCountInfo(final int count) {
		return String.format(" [%d of %d] ", count, MessageConstants.SENDING_COUNT);
	}
}
