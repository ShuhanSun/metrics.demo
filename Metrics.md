## What are metrics ?

>  Metrics are numeric measurements
>
>  **time series** mean that changes are recorded over time. 

Metrics helps to understanding why your application is working in a certain way. 

Lets assume you are running a web application. You need some information to find out what is happening with your application which is slow.

For example the application can become slow when the number of requests are high. 

If you have the request count metric you can spot the reason and increase the number of servers to handle the load.

## Micrometer

Micrometer is a metrics instrumentation library. Like SLF4J, but for application metrics !

https://micrometer.io/

### Common categories of metric

####  1. Counter
Counters report a single metric, a count, it can increment by a fixed positive amount.

For example:

- number of requests served
- tasks completed
- errors.

#### 2. Timer
Timer measuring short-duration latencies, and the frequency of such events.

For example:

- request latency to a web server
- execution time of a small task

##### Percentiles

Timers and distribution summaries support collecting data to observe their percentile distributions

```java
Timer.builder("my.timer")
   .publishPercentiles(0.5, 0.95) // median and 95th percentile. This is used to publish percentile values computed in your app. 
```



#### 3. Gauge
A gauge is a metric that represents a single numerical value that can arbitrarily go up and down.

Typical examples:

- size of a collection or map
- number of threads in a running state.
- current memory usage



#### [Dome code](https://github.com/ShuhanSun/metrics.demo)



## Prometheus

Prometheus is a monitoring system and in-memory dimensional time series database. 

It **collects** (by pull model) metrics from application instances periodically and **stores** metrics as **time series data**. 

https://prometheus.io/

#### Install

Download  [Prometheus](https://prometheus.io/download/) 

Update the config file "prometheus.yml" 

```yml
global:
  scrape_interval:     15s
  evaluation_interval: 15s 
scrape_configs:
  - job_name: 'metrics-demo-local'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['localhost:8080']
```
Start the prometheus server

```bash
./prometheus --config.file=prometheus.yml
```

#### UI

 http://localhost:9090/graph

#### PromQL

It provides PromQL to query the metrics.



## Grafana

It is a visualization platform for dashboards and metrics. It is called Sherlock.io in eBay

- https://grafana.com/

#### Install

We can easy install and start grafana on Mac

```bash
brew install grafana
brew services start grafana
brew services stop grafana
```

#### UI

http://localhost:3000/

#### Add Data source of Prometheus 

Prometheus - http://localhost:9090

#### Add panel

PromQL query



> Add alerting
> add norification channels
Type: Email, Pagerduty, Slack
