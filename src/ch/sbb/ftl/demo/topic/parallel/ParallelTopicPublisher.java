package ch.sbb.ftl.demo.topic.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import com.tibco.ftl.FTLException;
import com.tibco.ftl.Message;
import com.tibco.ftl.Publisher;
import com.tibco.ftl.Realm;

import ch.sbb.ftl.demo.helper.FtlHelper;
import ch.sbb.ftl.demo.helper.MessageConstants;
import ch.sbb.ftl.demo.helper.MessagePayloadHelper;

public class ParallelTopicPublisher {

	public static final AtomicInteger messageCount = new AtomicInteger();

	private static final RandomSelector rand = new RandomSelector( //
			Integer.parseInt(System.getProperty("minQueue", "1")), //
			Integer.parseInt(System.getProperty("maxQueue", "50")) //
	);

	public void go() throws FTLException, InterruptedException {
		FtlHelper.setupLogging(Level.FINER);
		System.out.println("FTL TopicPublisher initializing... with: " + FtlHelper.realmServer);

		final Realm realm = FtlHelper.getRealm();
		System.out.println("Connected to: " + realm);
		Thread.sleep(300);

		// monitor sending statistics
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			final int count = messageCount.getAndSet(0);
			System.out.printf("%,d msg/s%n", count);
		}, 0, 1, TimeUnit.SECONDS);

		runInParallel(realm);
	}

	private void runInParallel(final Realm realm) {
		final ExecutorService executor = Executors.newFixedThreadPool(MessageConstants.PARALLEL_THREADS);
		for (int i = 0; i < MessageConstants.PARALLEL_THREADS; i++) {
			executor.submit(run(realm));
		}
		executor.shutdown();
	}

	private Runnable run(final Realm realm) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					send(realm);
				} catch (final Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}
		};
	}

	public void send(final Realm realm) throws FTLException, InterruptedException {
		final Publisher pub = realm.createPublisher(FtlHelper.ftlEndPoint);
		for (int i = 1; i <= MessageConstants.SENDING_COUNT; i++) {
			final Message msg = createMessage(realm, i);
			pub.send(msg);
			// Thread.sleep(100);
			msg.destroy();
			messageCount.incrementAndGet();
		}

		System.out.println("  Cool down");
		Thread.sleep(5000);
		pub.close();
		realm.close();
		Thread.sleep(5000);
		System.out.println("  DONE");
	}

	private static Message createMessage(final Realm realm, final int i) throws FTLException {
		final String destination = rand.getRandomDestination();
		final String text = rand.createMessage(Integer.parseInt(System.getProperty("msgSize", "0")));
		final String payload = MessagePayloadHelper.createPayload(text, i, destination);
		return createTextMessage(realm, i, payload, destination);
	}

	private static Message createTextMessage(final Realm realm, final int i, final String text, final String typeName)
			throws FTLException {

		final Message msg = realm.createMessage("lastest");
		msg.setString("type", typeName);
		msg.setString("payload", text);
		msg.setLong("number", Long.valueOf(i));
		return msg;
	}

	public static void main(final String[] args) throws Exception {
		new ParallelTopicPublisher().go();
	}
}
