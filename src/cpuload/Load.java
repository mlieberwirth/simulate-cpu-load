package cpuload;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Load
{
    private static final int NUMBER_CORES = 2;
    private static final int NUMBER_THEADS_PER_CORE = 2;
    private static final double SIMULATED_LOAD_PERCENT = 0.6;
    private static final long SIMULATION_RUNTIME_SEC = 10;
    private static final long LOGGING_INTERVAL_SEC = 2;

    private final MBeanServer mbs;

    private Load()
    {
        mbs = ManagementFactory.getPlatformMBeanServer();
    }
    
    private void simulateLoad()
    {
        System.out.println("Will run: " + SIMULATION_RUNTIME_SEC + "sec");
        System.out.println("Target cpu is: " + SIMULATED_LOAD_PERCENT * 100 + "%");

        int maxThreads = NUMBER_CORES * NUMBER_THEADS_PER_CORE;
        for (int thread = 0; thread < maxThreads; thread++)
        {
            System.out.println("Start thread number: " + (thread + 1) + " of " + maxThreads);
            new BusyThread("Thread" + thread, SIMULATED_LOAD_PERCENT, SIMULATION_RUNTIME_SEC).start();
        }

        long startTime = System.currentTimeMillis();

        while(System.currentTimeMillis() - startTime < SIMULATION_RUNTIME_SEC * 1_000)
        {
            System.out.println("Running: " + (System.currentTimeMillis() - startTime) / 1_000 + " of "
                            + SIMULATION_RUNTIME_SEC + " sec " + getCurrentCpuLoad() + "% cpu");
            sleep(LOGGING_INTERVAL_SEC * 1_000);
        }
    }

    private Double getCurrentCpuLoad()
    {
        try
        {
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

            if (list.isEmpty())
            {
                return Double.NaN;
            }

            Attribute att = (Attribute)list.get(0);
            Double value = (Double)att.getValue();

            // usually takes a couple of seconds before we get real values
            if (value == -1.0)
            {
                return Double.NaN;
            }
            // returns a percentage value with 1 decimal point precision
            return ((int)(value * 1000) / 10.0);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0d;
    }

    private void sleep(long milis)
    {
        try
        {
            Thread.sleep(milis);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Thread that actually generates the given load
     */
    private static class BusyThread extends Thread
    {
        private double load;
        private long durationSec;

        /**
         * Constructor which creates the thread
         */
        public BusyThread(String name, double load, long durationSec)
        {
            super(name);
            this.load = load;
            this.durationSec = durationSec;
        }

        /**
         * Generates the load when run
         */
        @Override
        public void run()
        {
            long startTime = System.currentTimeMillis();
            try
            {
                // Loop for the given duration
                while(System.currentTimeMillis() - startTime < durationSec * 1000)
                {
                    // Every 100ms, sleep for the percentage of unladen time
                    if (System.currentTimeMillis() % 100 == 0)
                    {
                        Thread.sleep((long)Math.floor((1 - load) * 100));
                    }
                }
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts the load simulation
     */
    public static void main(String[] args)
    {
        new Load().simulateLoad();
    }
}
