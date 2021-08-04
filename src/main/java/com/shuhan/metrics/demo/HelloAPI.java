package com.shuhan.metrics.demo;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidParameterException;
import java.util.*;

@RestController
public class HelloAPI {
    /**
     * 1. Counter
     */
    private static final Counter COUNTER = Metrics.counter("hello.counter", Tags.of("func", "hello"));
    private static final Counter COUNTER_EXCEPTION = Metrics.counter("hello.counter.exception", Tags.of("func", "hello"));

    /**
     * 2. Timer
     */
    private static final Timer TIMER = Metrics.timer("hello.timer", Tags.of("func", "hello"));
    private static final Timer TIMER_SAMPLE = Metrics.timer("hello.timer.sample", Tags.of("func", "hello"));
    private static final Timer TIMER_PERCENTILES = Timer.builder("hello.timer.percentiles")
            .tag("func", "hello")
            .publishPercentiles(0.5, 0.95, 0.99) // This is used to publish percentile values computed in your app.
//            .publishPercentileHistogram() // This is used to publish a histogram suitable for computing aggregable (across dimensions, aka tags) percentile approximations
            .register(Metrics.globalRegistry);

    /**
     * 3. Gauge
     */
    private static final List<String> LIST = Metrics.gaugeCollectionSize("hello.list.gauge", Tags.of("func", "hello"), new ArrayList<>());
    //    private static final List<String> LIST = Metrics.globalRegistry.gauge("hello.list.gauge", Tags.of("func", "hello"), new ArrayList<>(), List::size);
    //    private static final Map<String, Integer> MAP = Metrics.gaugeMapSize("hello.list.gauge", Tags.of("func", "hello"), new HashMap<>());

    @Timed(value = "hello.timer.annotation")
    @GetMapping("/hello")
    public String hello(@RequestParam("param") final String param) {
        // 1. Counter
        COUNTER.increment();

        // 2. Timer: Recording blocks of code
        String r;
        try {
            r = TIMER.record(() -> func(param));
        }
        catch (Exception e) {
            COUNTER_EXCEPTION.increment();
            throw e;
        }

        // Timer: Storing start state in Timer.Sample
        Timer.Sample start = Timer.start();
        func("test");
        start.stop(TIMER_SAMPLE);

        // Timer: percentiles
        TIMER_PERCENTILES.record(()-> {
            try {
                Thread.sleep(new Random().nextInt(200));
            }
            catch (InterruptedException ignored) {
            }
        });

        // 3. Gauge
        LIST.add(param);
        return r;
    }

    private String func(final String param) throws InvalidParameterException {
        if (param == null || param.length() == 0) {
            throw new InvalidParameterException();
        }
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException ignored) {
        }
        return "result: " + param;
    }
}
