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
     * 2. Timer 1: Recording blocks of code
     */
    private static final Timer TIMER = Metrics.timer("hello.timer", Tags.of("func", "hello"));

    @GetMapping("/hello")
    public String hello(@RequestParam("param") final String param) {
        // Counter 1 of request
        COUNTER.increment();

        String funcResult;
        try {
            // func is the function to execute and measure the execution time.
            // return the result of func
            // throw exception if param is no empty
            funcResult = TIMER.record(() -> func(param));
        }
        catch (Exception e) {
            // Counter 2 of exception
            COUNTER_EXCEPTION.increment();
            throw e;
        }

        return funcResult;
    }

    /** Timer 2: Sample */
    private static final Timer TIMER_SAMPLE = Metrics.timer("hello.timer.sample", Tags.of("func", "hello"));

    @GetMapping("/hello/timer2")
    public String helloTimer2() {
        // Storing start state in Timer.Sample
        Timer.Sample start = Timer.start();
        func("test");
        start.stop(TIMER_SAMPLE);
        return "hello.timer.sample";
    }

    /** Timer 3: annotation {@code TimedConfiguration} */
    @Timed(value = "hello.timer.annotation")
    @GetMapping("/hello/timer3")
    public String helloTimer3() {
        func("test");
        return "hello.timer.annotation";
    }

    /** Timer 4: percentiles */
    private static final Timer TIMER_PERCENTILES = Timer.builder("hello.timer.percentiles")
            .tag("func", "hello")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(Metrics.globalRegistry);

    @GetMapping("/hello/timer4")
    public String helloTimer4() {
        // Timer 3: with percentiles
        TIMER_PERCENTILES.record(() -> {
            try {
                Thread.sleep(new Random().nextInt(200));
            }
            catch (InterruptedException ignored) {
            }
        });

        return "hello.timer.percentiles";
    }

    /**
     * 3. Gauge
     */
    private static final List<String> LIST = Metrics.gaugeCollectionSize("hello.list.gauge", Tags.of("func", "hello"), new ArrayList<>());
    //    private static final List<String> LIST = Metrics.globalRegistry.gauge("hello.list.gauge", Tags.of("func", "hello"), new ArrayList<>(), List::size);
    //    private static final Map<String, Integer> MAP = Metrics.gaugeMapSize("hello.list.gauge", Tags.of("func", "hello"), new HashMap<>());

    @GetMapping("/gauge/add")
    public String addList() {
        LIST.add("a");
        return "LIST size is : " + LIST.size();
    }

    @GetMapping("/gauge/remove")
    public String removeList() {
        if (!LIST.isEmpty()) {
            LIST.remove(0);
        }

        return "LIST size is : " + LIST.size();
    }

    /**
     * the function to execute and measure the execution time.
     * @param param param
     * @return result
     * @throws InvalidParameterException no param
     */
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
