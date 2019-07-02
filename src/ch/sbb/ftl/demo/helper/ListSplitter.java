package ch.sbb.ftl.demo.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Klasse um Listen in mehrere Sublisten mit maximalen Eintr�gen zu unterteilen
 */
public final class ListSplitter {

	private ListSplitter() {

	}

	/**
	 * @param list
	 *            List die aufgeteilt werden soll
	 * @param chunkSize
	 *            maximale gr�sse der zur�ckgegebenen listen
	 * @return List mit Sublisten
	 */
	public static <T> List<List<T>> split(final List<T> list, final int chunkSize) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}

		if (list.size() <= chunkSize) {
			return Collections.singletonList(list);
		}

		final int size = list.size();
		return IntStream.range(0, (size - 1) / chunkSize + 1)
				.mapToObj(i -> list.subList(i *= chunkSize, size - chunkSize >= i ? i + chunkSize : size))
				.collect(Collectors.toList());
	}

	/**
	 * @param list
	 *            List die aufgeteilt werden soll
	 * @param chunks
	 *            maximale anzahl der zur�ckgegebenen listen
	 * @return List mit Sublisten
	 */
	public static <T> List<List<T>> chunk(final List<T> list, int chunks) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}

		if (list.size() <= chunks) {
			chunks = list.size();
		}

		final int chunkSize = list.size() / chunks;

		final int size = list.size();
		return IntStream.range(0, (size - 1) / chunkSize + 1)
				.mapToObj(i -> list.subList(i *= chunkSize, size - chunkSize >= i ? i + chunkSize : size))
				.collect(Collectors.toList());
	}

	/**
	 * @param set
	 *            Set die aufgeteilt werden soll
	 * @param chunkSize
	 *            maximale gr�sse der zur�ckgegebenen Sets
	 * @return Map mit Subsets
	 */
	@SuppressWarnings("boxing")
	public static <T> Map<Integer, Set<T>> parition(final Set<T> set, final int chunkSize) {

		final Map<Integer, Set<T>> map = new HashMap<>();
		if (set == null || set.isEmpty() || chunkSize <= 0) {
			return map;
		}
		int paritionId = 1;
		if (set.size() < chunkSize) {
			map.put(paritionId, set);
		} else {
			final Iterator<T> iter = set.iterator();
			int counter = 0;
			map.put(paritionId, new HashSet<>());
			while (iter.hasNext()) {
				if (counter >= chunkSize) {
					paritionId++;
					counter = 0;
					map.put(paritionId, new HashSet<>());
				}
				map.get(paritionId).add(iter.next());
				counter++;
			}
		}
		return map;
	}

}