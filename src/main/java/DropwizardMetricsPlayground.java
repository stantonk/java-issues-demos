import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.TimeUnit;

/**
 * Created by kevin on 3/15/18.
 */
public class DropwizardMetricsPlayground {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("starting..");
        MetricRegistry metricRegistry = new MetricRegistry();
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(5, TimeUnit.SECONDS);
        Timer t = metricRegistry.timer("silly");
        while (true) {
            Timer.Context context = t.time();
            Thread.sleep(1000l);
            context.stop();
        }
    }
}
