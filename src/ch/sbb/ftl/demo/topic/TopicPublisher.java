package ch.sbb.ftl.demo.topic;

import java.util.logging.Level;

import com.tibco.ftl.FTLException;
import com.tibco.ftl.Message;
import com.tibco.ftl.Publisher;
import com.tibco.ftl.Realm;

import ch.sbb.ftl.demo.helper.FtlHelper;
import ch.sbb.ftl.demo.helper.MessageConstants;
import ch.sbb.ftl.demo.helper.MessagePayloadHelper;

public class TopicPublisher {

	public static void main(final String... args) throws FTLException, InterruptedException {
		FtlHelper.setupLogging(Level.WARNING);
		System.out.println("FTL TopicPublisher initializing... with: " + FtlHelper.realmServer);
		final Realm realm = FtlHelper.getRealm();
		System.out.println("Connected to: " + realm);

		MessageConstants.DataType msgSize = MessageConstants.DataType.K10_TextMessage;
		if (System.getProperty("msgSize") != null) {
			msgSize = MessageConstants.DataType.valueOf(System.getProperty("msgSize"));
		}

		final Publisher pub = realm.createPublisher(FtlHelper.ftlEndPoint);
		runWithNewSession(pub, realm, FtlHelper.TYPE_NAME, msgSize);

		System.out.println("Cool down");
		Thread.sleep(500);
		pub.close();
		realm.close();
		System.out.println("DONE");
	}

	private static void runWithNewSession(final Publisher pub, final Realm realm, final String typeName,
			final MessageConstants.DataType dataType) {

		final int delay = Integer.parseInt(System.getProperty("delay"));

		try {
			for (int i = 1; i <= MessageConstants.SENDING_COUNT; i++) {
				final Message msg = createMessage(realm, dataType, i, typeName);
//				System.out.println(msg.toString().trim().substring(0, 80));
				pub.send(msg);
				msg.destroy();
				System.out.println(calcCountInfo(i) + "MessageId-" + i + " sent");

				if (delay > 0) {
					Thread.sleep(delay);
				}
			}
		} catch (final Exception e) {
			System.out.println(e);
		}
	}

	private static Message createMessage(final Realm realm, final MessageConstants.DataType dataType, final int i,
			final String typeName) throws FTLException {

		final String payload = MessagePayloadHelper.createPayload(dataType, i, typeName);
		return createTextMessage(realm, i, payload, typeName);
	}

	private static Message createTextMessage(final Realm realm, final int i, final String text, final String typeName)
			throws FTLException {

		final Message msg = realm.createMessage("lastest");
		msg.setString("type", typeName);
		msg.setString("payload", text);
		msg.setLong("number", Long.valueOf(i));
		return msg;
	}

	private static String calcCountInfo(final int count) {
		return String.format(" [%d of %d] ", count, MessageConstants.SENDING_COUNT);
	}
}
