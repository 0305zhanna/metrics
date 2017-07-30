package metrics;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Created by User on 29.07.2017.
 */
public class Worker3 implements Runnable {

    private Semaphore full;
    private Semaphore empty;
    private Queue<List<Pair>> buffer;
    private long total;

    List<Pair> PoisonPill = Collections.EMPTY_LIST;


    public Worker3(Semaphore full, Semaphore empty, Queue<List<Pair>> buffer){
        this.full = full;
        this.empty=empty;
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
//            System.out.println("Worker:'I have a permit!'");
            List<Pair> pairs = buffer.poll();
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
