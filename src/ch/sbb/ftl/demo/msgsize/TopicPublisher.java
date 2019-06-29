package ch.sbb.ftl.demo.msgsize;

import java.util.logging.Level;

import com.tibco.ftl.FTLException;
import com.tibco.ftl.Message;
import com.tibco.ftl.Publisher;
import com.tibco.ftl.Realm;

public class TopicPublisher {

	public static void main(final String... args) throws FTLException, InterruptedException {
		FtlHelper.setupLogging(Level.WARNING);
		System.out.println("FTL TopicPublisher initializing... with: " + FtlHelper.realmServer);
		final Realm realm = FtlHelper.getRealm();
		System.out.println("Connected to: " + realm);
		Thread.sleep(300);

		final Publisher pub = realm.createPublisher(FtlHelper.ftlEndPoint);
		runWithNewSession(pub, realm, FtlHelper.TYPE_NAME, MessageConstants.DataType.K100_TextMessage);
		
		System.out.println("Cool down");
		Thread.sleep(5000);
		pub.close();
		realm.close();
		Thread.sleep(5000);
		System.out.println("DONE");
	}

	private static void runWithNewSession(final Publisher pub, final Realm realm, final String typeName,
			final MessageConstants.DataType dataType) {

		try {
			for (int i = 1; i <= MessageConstants.SENDING_COUNT; i++) {
				final Message msg = createMessage(realm, dataType, i, typeName);
				System.out.println(msg.toString().trim().substring(0, 80));
				pub.send(msg);
				msg.destroy();
//				System.out.println(calcCountInfo(i) + "MessageId-" + i + " sent");
//				Thread.sleep(100);
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
