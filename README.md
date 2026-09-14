# Code Review Practice

Central repo for daily code review practice. One Maven module per exercise.

## Layout

```
pom.xml                        parent (packaging: pom, lists the day modules)
day-01-user-service/
  pom.xml
  src/main/java/day01/
    model/                     shared supporting classes (Order, OrderRepository, ...)
    original/Original.java     code under review - leave it broken
    fixed/Fixed.java           your corrected version - this is what you push
```

Both `Original.java` and `Fixed.java` declare a package-private `OrderExporter`, so
they live in separate packages (`day01.original`, `day01.fixed`) and compile side by
side. That lets you diff the two and keep the buggy version around as the reference.

## Build

```
mvn clean verify
```

Requires JDK 17+ (`maven.compiler.release` is 17).

## Adding a new exercise

1. Copy `day-01-user-service/` to `day-NN-<topic>/` and update its `<artifactId>`.
2. Add `<module>day-NN-<topic></module>` to the parent `pom.xml`.
3. Drop the code under review into `original/`, work your fix in `fixed/`.
