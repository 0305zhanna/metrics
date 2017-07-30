package metrics;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Metrics2 {

	private static final int QUEUE_CAPACITY = 2;
	public static final int MAX_PAIRS = 1000;
	private static int total = 0;
	public static void main(final String[] args) {

		System.out.println("Start");

		long start = System.nanoTime();

		Metrics2 metrics = new Metrics2();

		metrics.doWork();

		long stop = System.nanoTime();
		System.out.println("Finish");
		System.out.println("Elapsed: " + (stop - start));
	}


	private void doWork() {
		Data data = new Data();
		ReferenceSequence sequence = new ReferenceSequence();

		Queue<Pair> buffer = new LinkedBlockingQueue<>();

		ExecutorService service = Executors.newCachedThreadPool();

		Semaphore full = new Semaphore(0);
		Semaphore empty = new Semaphore(10);
		Semaphore selfexcept = new Semaphore(1);

		List<Worker> workers = Arrays.asList(new Worker(full,empty,selfexcept,buffer),new Worker(full,empty,selfexcept,buffer),
														new Worker(full,empty,selfexcept,buffer));

		for(Worker worker:workers) {
			service.execute(worker);//запустить работника
		}

		for (Record record : data) {
			Reference ref = sequence.getRef(record);

			empty.acquireUninterruptibly();

			Pair pair = new Pair(record,ref);
			buffer.add(pair);

			full.release();
		}

		//System.out.println(worker.getTotal());
		service.shutdown();
		for(Worker worker:workers){
			worker.stop();
		}
	}


}
