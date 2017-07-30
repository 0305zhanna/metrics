package metrics;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Created by User on 29.07.2017.
 */
public class Worker4 implements Runnable {

    private Semaphore full;
    private Semaphore empty;
    private Semaphore selfexcept;
    private Queue<List<Pair>> buffer;
    private long total;

    List<Pair> PoisonPill = Collections.EMPTY_LIST;


    public Worker4(Semaphore full, Semaphore empty,Semaphore selfexcept1, Queue<List<Pair>> buffer){
        this.full = full;
        this.empty=empty;
        this.selfexcept = selfexcept1;
        this.buffer=buffer;
    }

    public long getTotal(){
        return total;
    }

    @Override
    public void run() {
        System.out.println("Worker:'I'm starting...'");
        while (true){
            full.acquireUninterruptibly();
            selfexcept.acquireUninterruptibly();

            List<Pair> pairs = buffer.poll();
            selfexcept.release();
            empty.release();

            if(pairs == PoisonPill){
                System.out.println("Worker:'I'm returning!'");
                return;
            }

            for(Pair pair:pairs){
                total += process(pair.getRec(),pair.getRef());
            }

        }
    }

    private int process(final Record record, final Reference ref) {
        int sum = 0;
        for (int i = 0; i < record.read.length; i++) {
            sum += record.read[i];
            sum += ref.read[i];
        }
        return sum;
    }


    public void stop(){
        empty.acquireUninterruptibly();
        buffer.add(PoisonPill);
        full.release();
    }
}
