package metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Metrics3 {

	private static final int QUEUE_CAPACITY = 2;
	public static final int MAX_PAIRS = 500;
	private static int total = 0;
	public static void main(final String[] args) {

		System.out.println("Start");

		long start = System.nanoTime();

		Metrics3 metrics = new Metrics3();

		metrics.doWork();

		long stop = System.nanoTime();
		System.out.println("Finish");
		System.out.println("Elapsed: " + (stop - start));
	}


	private void doWork() {
		Data data = new Data();
		ReferenceSequence sequence = new ReferenceSequence();

		Queue<List<Pair>> buffer = new LinkedBlockingQueue<>();

		ExecutorService service = Executors.newSingleThreadExecutor();

		Semaphore full = new Semaphore(0);
		Semaphore empty = new Semaphore(50);

		Worker3 worker = new Worker3(full,empty,buffer);
		service.execute(worker);//запустить работника

		List<Pair> pairs = new ArrayList<>(MAX_PAIRS);
		int pairsCount = 0;

		for (Record record : data) {
			Reference ref = sequence.getRef(record);

			Pair pair = new Pair(record,ref);//читаем
			pairs.add(pair);//формируем блок данных для обработки

			pairsCount++;
//			System.out.println("lists: " + pairsCount);

			if (pairs.size() < MAX_PAIRS) {
				continue;
			}

			empty.acquireUninterruptibly();//берем одно разрешение
//			System.out.println("main:have a permit");
			buffer.add(pairs);
			pairs = new ArrayList<>(MAX_PAIRS);
			full.release();//дам разрешение, что в буфере есть блок
		}

		System.out.println(worker.getTotal());
		service.shutdown();
		worker.stop();
	}


}
