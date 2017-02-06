package com.cosd.greenbuild.calwin.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * An application that uses processor threads to get things done. A given number
 * of processor threads are created and added to the idle queue. Each thread is
 * then taken and assigned a task and added to the busy queue. When the
 * processing finishes, the processor removes itself from the busy queue,
 * inserts itself into the idle queue and notifies of its availability. The
 * newly idle thread is then brought back into use.
 * <p/>
 * Descendants must implement behaviour to create processors and start
 * processing.
 *
 * @author Andy.Taylor
 *
 */
public abstract class MultiThreadedApp extends Application {

	static int ids = 1;

	/**
	 * A general processor which can be started and do stuff. Descendants should
	 * implement the data assignment and processing.
	 *
	 * @author Andy.Taylor
	 *
	 */
	public static abstract class Processor implements Runnable {

		public static final Logger log = Logger.getLogger(Processor.class);

		protected final int id;
		private int numRuns = 0;
		protected Throwable error;
		private Thread thread;

		private MultiThreadedApp svr;

		private long waitTime = 0;

		public Processor() {
			this.id = MultiThreadedApp.ids++;
		}

		/**
		 * Should be called by a public process(Object... args) method as
		 * defined in the descendant class. The parameters of the public method
		 * should be sufficient to provide input required before processing, and
		 * should then call super.process();
		 *
		 * @return thread in which the process is running
		 */
		protected Thread process() {
			this.thread = new Thread(this, this.getClass().getSimpleName() + id);
			thread.setDaemon(true);
			thread.start();
			return thread;
		}

		/**
		 * @return the thread associated with the last call to process
		 */
		public Thread getThread() {
			return thread;
		}

		public int getId() {
			return id;
		}

		public void run() {
			numRuns++;
			long startTime = System.currentTimeMillis();
			log.info(id + ") Processor has started run: " + numRuns);
			try {
				doProcessing();
				if (this.waitTime > 0)
					Thread.currentThread().wait(this.waitTime);
			} catch (Throwable e) {
				log.error("Error during processing", e);
				e.printStackTrace();
				this.error = e;
			} finally {
				long runTime = System.currentTimeMillis() - startTime;
				if (error == null) {
					log.info(id + ") Processor has finished run: " + numRuns + " in " + runTime + " ms");
				} else {
					String msg = id + ") Processor has finished run: " + numRuns + " in " + runTime + " ms with error.";
					log.error(msg, error);
				}
				if (svr != null) {
					svr.returnProcessor(this);
				}
			}
		}

		/**
		 * Override to implement specific processing.
		 *
		 * @throws Exception
		 */
		protected abstract void doProcessing() throws Exception;

		public int getProcessorId() {
			return id;
		}

		public Throwable getError() {
			return error;
		}

	}

	protected Argument numProcsArg;
	// processors that are ready to process
	final List<Processor> idleProcessors = new Vector<Processor>();
	// processors that are currently processing
	private final List<Processor> busyProcessors = new Vector<Processor>();
	protected boolean running = true;
	private final Argument waitArg;
	private int waitTime = 0;
	// time of last error. if less that ERROR_WINDOW (60s), added to numErrors.
	// if numErrors > MAX_ERRORS (3) then wait for ERROR_WAIT (60s)
	private long lastError;
	private final int numErrors = 0;
	// time that last error occured or same if within 10m window
	private long lastErrorTime = 0;
	// number of errors in last 10m, if over threshold, sleep
	private int lastErrorCount = 0;
	// when too many errors, sleep until this time.
	private long sleepUntil = 0;
	// num of ms in which errors are considered to be part of error flood
	private static int ERROR_WINDOW = 60 * 1000;
	// maximum number of errors in error window before considered a flood
	private static int MAX_ERRORS = 3;
	// amount of time to wait on error flood
	private static int ERROR_WAIT = 60 * 1000;

	public MultiThreadedApp() {
		super();
		addNumProcsArg();
		waitArg = addArg("--wait", "0", "Wait time in ms between processing, 0 for no wait.");
	}

	protected void addNumProcsArg() {
		numProcsArg = addArg("--processors", "5", "Number of Adobe processors.");
	}

	public synchronized void returnProcessor(Processor processor) {
		if (processor.error != null) {
			onError(processor.error);
		}

		// only return to idle when still running.  will shutdown when busy and idle are empty
		busyProcessors.remove(processor);
		if (running)
			idleProcessors.add(processor);
		else if (!busyProcessors.isEmpty())
			log.info("Gradual shutdown, "+busyProcessors.size()+" processors still to finish.");

		synchronized (idleProcessors) {
			idleProcessors.notify();
		}
	}

	private void onError(Throwable throwable) {
		log.warn("Error while processing", throwable);
		// if there was no last error time, or last error time is older that
		// 10m,
		if (this.lastErrorTime == 0 || System.currentTimeMillis() > this.lastErrorTime + 10 * 60 * 1000) {
			// stamp new lastErrorTime
			this.lastErrorTime = System.currentTimeMillis();
			this.lastErrorCount = 0;
		}
		this.lastErrorCount++;
		if (this.lastErrorCount > 10) {
			log.warn("Too many errors too quickly!  Going to sleep for 10mins.");
			this.sleepUntil = System.currentTimeMillis() + 10 * 60 * 1000;
		}
	}

	@Override
	protected void onShutdown() {
		log.info("Shutdown triggered.  Waiting for threads.");
		running = false;
		while (!busyProcessors.isEmpty()) {
			synchronized (Thread.currentThread()) {
				try {
					Thread.currentThread().wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected int doRun() throws Exception {
		this.waitTime = waitArg.getIntValue();
		for (Processor proc:createProcessors()) {
			proc.waitTime = this.waitTime;
			proc.svr = this;
			idleProcessors.add(proc);
		}
		log.info(this.getClass().getSimpleName() + " started with: " + idleProcessors.size() + " "
				+ idleProcessors.get(0).getClass().getSimpleName() + " processors.");

		while (true) {
			try {
				Processor aProc = takeAProcessor();
				if (aProc != null) {
					if (!processWith(aProc)) {
						log.info("Shudown indicated.  Waiting for completion.");
						running = false;
					}
				}
				if (!running) {
					if (busyProcessors.isEmpty()) {
						log.info("All processors finished, shutting down.");
						break;
					} else {
						synchronized (idleProcessors) {
							try {
								idleProcessors.wait(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

				}
			} catch (Exception e) {
				onError(e);

			}
			if (sleepUntil > System.currentTimeMillis()) {
				long sleepMS = sleepUntil - System.currentTimeMillis();
				log.warn("Too many errors, sleeping until " + new Date(sleepUntil));
				synchronized (Thread.currentThread()) {
					Thread.sleep(sleepMS);
				}
			}
		}

		return 0;
	}

	protected List<Processor> createProcessors() {
		if (numProcsArg==null)
			throw new NullPointerException("--num-procs is required and not included.  ensure addNumProcsArg() is called");
		List<Processor> procs=new ArrayList<Processor>();
		for (int i = 0; i < numProcsArg.getIntValue(); i++) {
			Processor proc = createAProcessor();
			procs.add(proc);
		}
		return procs;
	}

	/**
	 * Create a new specific processor.
	 *
	 * @return a new processor
	 */
	protected Processor createAProcessor() {
		throw new RuntimeException("Implement createAProcessor() in "+this.getClass().getName()+" if not overriding createProcessors()");
	}

	/**
	 * Begin processing on a given processor. Should call the custom public
	 * process(Object...) method which begins processing.
	 *
	 * @param proc
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean processWith(Processor proc) throws Exception;

	protected Processor takeAProcessor() throws Exception {
		while (true) {
			if (idleProcessors.size() > 0) {
				Processor tp = idleProcessors.remove(0);
				if (running) {
					busyProcessors.add(tp);
					tp.svr = this;
					return tp;
				}
			} else if (!running) {
//				log.info("All finished, shutting down");
				running = false;
				return null;
			}
			if (idleProcessors.isEmpty()) {
				log.debug("All busy, waiting for a processor to finish.");
				synchronized (idleProcessors) {
					try {
						idleProcessors.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
