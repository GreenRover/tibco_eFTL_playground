package ch.sbb.ftl.demo.topic.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.sbb.ftl.demo.helper.MessageConstants;

public class RandomSelector {

	private final List<String> messages = new ArrayList<>();
	private final Map<Integer, String> messageCache = new HashMap<>();
	private final List<String> topics = new ArrayList<>(100);

	protected final Random rand = new Random();
	protected int minQueue;
	protected int maxQueue;

	public RandomSelector(final int minQueue, final int maxQueue) {
		this.minQueue = minQueue;
		this.maxQueue = maxQueue;

		messages.add(MessageConstants.MESSAGE_B10);
		messages.add(MessageConstants.MESSAGE_B20);
		messages.add(MessageConstants.MESSAGE_B50);
		messages.add(MessageConstants.MESSAGE_B100);
		messages.add(MessageConstants.MESSAGE_B200);
		messages.add(MessageConstants.MESSAGE_B500);
		messages.add(MessageConstants.MESSAGE_K1);
		messages.add(MessageConstants.MESSAGE_K2);

		for (int i = minQueue; i <= maxQueue; i++) {
			// final String topic = ("topic/parallel/" + i);
			final String topic = ("demo");
			topics.add(topic);
		}
	}

	public String getRandomDestination() {
		return topics.get(getRandomIndex());
	}

	public List<String> getAllDestinations() {
		return new ArrayList<>(topics);
	}

	public int getRandomIndex() {
		return minQueue + rand.nextInt(maxQueue - minQueue);
	}

	public String createMessage(final int msgSize) {
		if (msgSize < 1) {
			final int index = rand.nextInt(messages.size());
			return messages.get(index);
		} else {
			return messageCache.computeIfAbsent(msgSize, s -> MessageConstants.createStringOfSize(s));
		}
	}
}
