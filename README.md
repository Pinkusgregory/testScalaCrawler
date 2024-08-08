# testScalaCrawler

# Dependencies

- Java JRE v.: `11.*` 
- Docker

# Run

```sh
sbt run
```

# Tests

```sh
sbt test
```

# Docker
```sh
docker build -t test-crawler-core .
docker run -dp 127.0.0.1:8010:8010 test-crawler-core
```