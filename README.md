## What are metrics ?

Metrics helps to understand why your application is working in a certain way. 

Lets assume we are running a web application. We need some information to find out what is happening with our application which is slow.

For example the application can become slow when the number of requests are high. 

If you have the request count metric you can spot the reason and increase the number of servers to handle the load.



## How the metrics monitor system works ?
We will add metrics and display them in a dashboard in local, to see how it works.

- Step 1: Add metrics in our application by **Micrometer** Prometheus. It will collect the data and expose the metrics in a endpoint in our application.
- Step 2: Start **Prometheus** server, It collects metrics from application instances periodically and **stores** metrics.  
- Step 3: Start **Grafana** which is a visualization platform, add panels for metrics by PromQL



## 1. Micrometer

Micrometer is a metrics instrumentation library. Like SLF4J, but for application metrics !

https://micrometer.io/

### Common categories of metric

####  1. Counter
The counter metric type is used for any value that **increases** by a fixed positive amount.

For example:

- number of requests served
- tasks completed
- number of errors.

#### 2. Timer
Timer is used to measure the short-duration latencies

For example:

- request latency to a web server
- execution time of a small task

#### 3. Gauge
The gauge metric type can be used for values that go up and down.

For example:

- size of a collection or map
- number of threads in a running state.
- current memory usage



#### [Dome code](https://github.com/ShuhanSun/metrics.demo)

Run dome code and request

- Conter and Timer: http://localhost:8080/hello?param=aaa
- Timer for sample: http://localhost:8080/hello/timer2
- Timer for annotaion: http://localhost:8080/hello/timer3
- Timer for percentiles: http://localhost:8080/hello/timer4
- Gauge add: http://localhost:8080/gauge/add
- Gauge remove: http://localhost:8080/gauge/remove
- Gauge thread : http://localhost:8080/gauge/thread

The Metrics exposed: http://localhost:8080/actuator/prometheus



## 2. Prometheus

Prometheus is a monitoring system and in-memory dimensional time series database. 

It **collects** (by pull model) metrics from application instances periodically and **stores** metrics as **time series data**.  

> **Time series** mean that changes are recorded over time. 

https://prometheus.io/

#### Install

Download  [Prometheus](https://prometheus.io/download/) 

Update the config file "prometheus.yml" 

```yml
global:
  scrape_interval:     5s
  evaluation_interval: 5s 
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

It provides [PromQL](https://prometheus.io/docs/prometheus/latest/querying/basics/) to query the metrics.



## 3. Grafana

It is a visualization platform for dashboards and metrics. 

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

admin - admin

#### Add Data source of Prometheus 

Prometheus - http://localhost:9090

#### Add panel

PromQL query

-  query to calculate the per second rate of requests averaged over the last 5 minutes:

```
rate(request_count[5m])
```

> the *rate* function calculates the per second rate of increase averaged over the provided time interval. It can only be used with counters.
