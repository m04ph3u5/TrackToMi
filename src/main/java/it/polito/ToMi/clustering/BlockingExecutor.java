package it.polito.ToMi.clustering;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class BlockingExecutor {

	@Autowired
	private ThreadPoolTaskExecutor executor;
	private Semaphore semaphore;
	private int availableThread;
	
	@PostConstruct
	private void initialize(){
		availableThread = executor.getMaxPoolSize();
		semaphore = new Semaphore(availableThread);
	}
	
	public void submit(final Runnable command)
            throws InterruptedException, RejectedExecutionException {
        semaphore.acquire();
        try {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
            throw e;
        }
    }
	
	public void finalSubmit(final Runnable command)
            throws InterruptedException, RejectedExecutionException {
        semaphore.acquire(availableThread);
        try {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        command.run();
                    } finally {
                        semaphore.release(availableThread);
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release(availableThread);
            throw e;
        }
    }
}
