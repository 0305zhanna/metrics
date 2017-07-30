package metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Metrics0 {

	private static final int QUEUE_CAPACITY = 2;
	public static final int MAX_PAIRS = 1000;

	public static void main(final String[] args) {

		System.out.println("Start");

		long start = System.nanoTime();

		Metrics0 metrics = new Metrics0();

		metrics.doWork();

		long stop = System.nanoTime();
		System.out.println("Finish");
		System.out.println("Elapsed: " + (stop - start));
	}

	private void doWork() {

		Data data = new Data();
		ReferenceSequence sequence = new ReferenceSequence();

		long total = 0;

		for(Record rec: data){
			Reference ref = sequence.getRef(rec);

			int sum = process(rec,ref);

			total+= sum;

		}
		System.out.println(total);
	}

	private int process(final Record record, final Reference ref) {
		int sum = 0;
		for (int i = 0; i < record.read.length; i++) {
			sum += record.read[i];
			sum += ref.read[i];
		}
		return sum;
	}

}
