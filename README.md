# Code Review Practice

Central repo for daily code review practice. One Maven module per exercise.

## Layout

```
pom.xml                              parent (packaging: pom, lists the day modules)
day-01-user-service/
  pom.xml
  src/main/java/day01/
    model/                           shared supporting classes (Order, OrderRepository, ...)
    original/Original.java           code under review - leave it broken
    fixed/OrderExporterFixed.java    your corrected version - this is what you push
day-02-payment-service/
  pom.xml
  src/main/java/day02/
    model/                           Order, Payment, PaymentResult, gateway + repository ports
    original/PaymentProcessor.java   code under review - leave it broken
    fixed/PaymentProcessorFixed.java your corrected version
day-03-add-discount-support/
  pom.xml
  src/main/java/day03/
    model/                           TaxRates (supporting constants)
    original/Order.java              code under review - leave it broken
    fixed/                           your corrected version goes here
day-04-rate-limiter/
  pom.xml
  src/main/java/day04/
    original/RateLimiterOriginal.java  code under review - leave it broken
    fixed/                           your corrected version goes here
```

Every module follows the same shape: `model/` holds the supporting types both
versions compile against, `original/` holds the code under review, `fixed/` holds
the reviewed result.

Original and fixed always live in separate packages (`dayNN.original`,
`dayNN.fixed`) so both compile side by side - in day 01 they even share the class
name `OrderExporter`. That lets you diff the two and keep the buggy version around
as the reference.

## Build

```
mvn clean verify
```

Requires JDK 17+ (`maven.compiler.release` is 17).

## Adding a new exercise

1. Copy `day-01-user-service/` to `day-NN-<topic>/` and update its `<artifactId>`
   and `<name>`.
2. Add `<module>day-NN-<topic></module>` to the parent `pom.xml`.
3. Rename the source package to `dayNN` and drop the code under review into
   `original/`, its supporting types into `model/`, and work your fix in `fixed/`.
