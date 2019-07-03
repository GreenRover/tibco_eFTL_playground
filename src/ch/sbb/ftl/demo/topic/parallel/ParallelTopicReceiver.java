package ch.sbb.ftl.demo.topic.parallel;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import com.tibco.ftl.ContentMatcher;
import com.tibco.ftl.EventQueue;
import com.tibco.ftl.FTLException;
import com.tibco.ftl.Message;
import com.tibco.ftl.Realm;
import com.tibco.ftl.Subscriber;
import com.tibco.ftl.SubscriberListener;

import ch.sbb.ftl.demo.helper.FtlHelper;
import ch.sbb.ftl.demo.helper.MessageConstants;

public class ParallelTopicReceiver {

	private static final RandomSelector rand = new RandomSelector( //
			Integer.parseInt(System.getProperty("minQueue", "1")), //
			Integer.parseInt(System.getProperty("maxQueue", "50")) //
	);

	public static AtomicInteger messageCountPerSecond = new AtomicInteger(0);
	public static AtomicInteger messageCount = new AtomicInteger(0);

	protected int MAX_MESSAGES = MessageConstants.PARALLEL_THREADS * MessageConstants.SENDING_COUNT;

	// statistics about priorities of received messages
	public static Map<Integer, Integer> map = new ConcurrentHashMap<>();

	private Realm realm;

	public void go() throws InterruptedException, FTLException {
		FtlHelper.setupLogging(Level.WARNING);
		System.out.println("FTL TopicSubscriber initializing...");

		realm = FtlHelper.getRealm();
		System.out.println("Connected to: " + realm);
		Thread.sleep(300);


		final EventQueue queue = realm.createEventQueue();
		
		subscribe(queue, rand.getAllDestinations());
		
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			final int countPerSecond = messageCountPerSecond.getAndSet(0);
			final int countTotal = messageCount.get();
			System.out.printf("%,d msg/s [%,d | %,d]%n", countPerSecond, countTotal, MAX_MESSAGES);
		}, 0, 1, TimeUnit.SECONDS);

		executorService.scheduleAtFixedRate(() -> {
			System.out.printf("  message prio stats: %s%n", calculateMapStatistics(map));
		}, 0, 30, TimeUnit.SECONDS);
		
		
		
		
		final ExecutorService executor = Executors.newFixedThreadPool(MessageConstants.PARALLEL_THREADS);
		for (int i = 0; i < MessageConstants.PARALLEL_THREADS; i++) {
			executor.submit(() -> {
				while (true) {
					queue.dispatch(1, TimeUnit.SECONDS);
				}
			});
		}
		executor.shutdown();
	}
	
	private void subscribe(final EventQueue queue, final List<String> destinations) throws FTLException {
		for (final String destination : destinations) {
			final String matcher = "{\"type\":\"" + destination + "\"}";
			final ContentMatcher cm = realm.createContentMatcher(matcher);

			final Subscriber sub = realm.createSubscriber(FtlHelper.ftlEndPoint, cm);
			queue.addSubscriber(sub, new TopicListener());
			
			cm.destroy();
		}
	}

	private String calculateMapStatistics(final Map<Integer, Integer> m) {
		final StringBuilder sb = new StringBuilder();
		final int sum = map.values().stream().mapToInt(Integer::intValue).sum();
		for (final Entry<Integer, Integer> el : m.entrySet()) {
			final int k = Integer.valueOf(el.getKey());
			final int v = Integer.valueOf(el.getValue());
			final float percent = ((float) v / sum) * 100.0f;
			sb.append(k).append("=").append(v).append(" ").append(Math.round(percent)).append("%, ");
		}
		return sb.toString();
	}

	private class TopicListener implements SubscriberListener {

		@Override
		public void messagesReceived(final List<Message> msgs, final EventQueue queue) {
			for (final Message msg : msgs) {
				try {
					processTextMessage(msg);
				} catch (final FTLException e) {
					System.out.printf("Consumer received exception: %s%n", e);
				}
			}
		}

		private void processTextMessage(final Message msg) throws FTLException {
			messageCountPerSecond.incrementAndGet();
			messageCount.incrementAndGet();
			final int messagePrio = 1; // TODO replace with message prio
			map.compute(messagePrio, (k, v) -> (v == null) ? 1 : v + 1);
		}
	}

	public static void main(final String[] args) throws InterruptedException, FTLException {
		new ParallelTopicReceiver().go();
	}
}
