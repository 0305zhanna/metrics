package metrics;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Created by User on 29.07.2017.
 */
public class Worker implements Runnable {

    private Semaphore selfexcept;
    private Semaphore full;
    private Semaphore empty;
    private Queue<Pair> buffer;
    private long total;

    Pair PoisonPill = new Pair(null,null);


    public Worker(Semaphore full, Semaphore empty,Semaphore selfexcept, Queue<Pair> buffer){
        this.full = full;
        this.empty=empty;
        this.buffer=buffer;
        this.selfexcept = selfexcept;
    }

    public long getTotal(){
        return total;
    }

    @Override
    public void run() {
        while (true){
            full.acquireUninterruptibly();
            selfexcept.acquireUninterruptibly();
            System.out.println(Thread.currentThread().getName()+":'I have permition' ");
            Pair pair = buffer.poll();
            if(pair == PoisonPill){
                System.out.println("Worker:'I'm returning!'");
                return;
            }
            selfexcept.release();
            empty.release();
            if(pair != null){
                if(pair != PoisonPill)
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
