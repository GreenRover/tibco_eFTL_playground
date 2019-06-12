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

	private static AtomicInteger messageCount = new AtomicInteger(1);

	public static void main(final String... args) throws FTLException {
		FtlHelper.setupLogging(Level.WARNING);

		System.out.println("TopicSubscriber initializing...");

		final Realm realm = FtlHelper.getRealm();
		
		final ContentMatcher cm = realm.createContentMatcher("{\"type\":\"" + FtlHelper.typeName + "\"}");

        // Create a subscriber
        final Subscriber sub = realm.createSubscriber(FtlHelper.ftlEndPoint, cm);

        // dispose the content matcher
        cm.destroy();

        // Create a queue and add subscriber to it
        final EventQueue queue = realm.createEventQueue();
        queue.addSubscriber(sub, new TopicSubscriber());
        
        System.out.println("TopicSubscriber start listing ...");
        while (true)
            queue.dispatch();
	}
	
	@Override
	public void messagesReceived(final List<Message> msgs, final EventQueue queue) {
		for (final Message msg : msgs) {
			final int count = messageCount.incrementAndGet();
			try {
				processTextMessage(msg, count);
			} catch (final FTLException e) {
				System.out.printf("Consumer received exception: %s%n", e);
			}
		}
	}

	private String calcCountInfo(final int count) {
		return String.format(" [%d of %d] ", count, MessageConstants.SENDING_COUNT);
	}

	private void processTextMessage(final Message msg, final int count) throws FTLException {
		final String countInfo = calcCountInfo(count);
		final int msgLength = msg.getString("payload").length();
		System.out.printf("%s TextMessage received: %s | %d bytes %n", countInfo, msg.getLong("number"), msgLength);
	}


}