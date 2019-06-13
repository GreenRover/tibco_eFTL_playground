package ch.sbb.ftl.demo.msgsize;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import com.tibco.ftl.ContentMatcher;
import com.tibco.ftl.EventQueue;
import com.tibco.ftl.FTLException;
import com.tibco.ftl.Message;
import com.tibco.ftl.Realm;
import com.tibco.ftl.Subscriber;
import com.tibco.ftl.SubscriberListener;

public class TopicSubscriber implements SubscriberListener {

	private static AtomicInteger messageCount = new AtomicInteger(0);

	public static void main(final String... args) throws FTLException {
		FtlHelper.setupLogging(Level.WARNING);

		System.out.println("FTL TopicSubscriber initializing...");

		final Realm realm = FtlHelper.getRealm();

		final ContentMatcher cm = realm.createContentMatcher("{\"type\":\"" + FtlHelper.TYPE_NAME + "\"}");

		// Create a subscriber
		final Subscriber sub = realm.createSubscriber(FtlHelper.ftlEndPoint, cm);

		// dispose the content matcher
		cm.destroy();

		// Create a queue and add subscriber to it
		final EventQueue queue = realm.createEventQueue();
		queue.addSubscriber(sub, new TopicSubscriber());

		System.out.println("FTL TopicSubscriber start listing ...");
		while (true)
			queue.dispatch();
	}

	@Override
	public void messagesReceived(final List<Message> msgs, final EventQueue queue) {
		for (final Message msg : msgs) {
			try {
				final int count = messageCount.incrementAndGet();
				processTextMessage(msg, count);
			} catch (final FTLException e) {
				System.out.printf("Consumer received exception: %s%n", e);
			}
		}
	}

	private static void processTextMessage(final Message msg, final int count) throws FTLException {
		final String payload = msg.getString("payload");
		MessagePayloadHelper.processPayload(payload, count);
	}
}