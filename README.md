# Проект 4-го спринта
![Apache Kafka](https://img.shields.io/badge/-Apache_Kafka-000?logo=apachekafka)
![Kafka Secured](https://img.shields.io/badge/-Kafka_Secured-28A745?logo=apachekafka&logoColor=white)
![Partition Balancing](https://img.shields.io/badge/-Partition_Balancing-0078D4?logo=apachekafka&logoColor=white)
![Spring Boot](https://img.shields.io/badge/-Spring_Boot-6DB33F?logo=springboot)

### Описание
Репозиторий предназначен для сдачи проекта 4-го спринта

### Как запустить контейнер
Сборка толстого jar файла:

```
gradlew clean kafka-security:bootJar
```

Запустите локально Docker:

```shell
cd infra; docker-compose up -d
```

### Балансировка партиций и диагностика кластера
#### Убедитесь, что все контейнеры запущены
```shell
docker ps
```

#### Определите текущее распределение партиций
```shell
docker exec -it kafka-0 kafka-topics --bootstrap-server localhost:9092 --command-config /etc/kafka/secrets/admin.properties --describe --topic balanced_topic
```

#### Копируем файл в контейнер
```shell
docker cp .\infra\config\reassignment.json kafka-0:/tmp/reassignment.json
```

#### Перераспределение партиций
```shell
docker exec -it kafka-0 kafka-reassign-partitions --bootstrap-server kafka-0:9092 --command-config /etc/kafka/secrets/admin.properties --reassignment-json-file /tmp/reassignment.json --execute
```

#### Проверка статуса перераспределения
```shell
docker exec -it kafka-0 kafka-reassign-partitions --bootstrap-server kafka-0:9092 --command-config /etc/kafka/secrets/admin.properties --reassignment-json-file /tmp/reassignment.json --verify
```

#### Проверка нового распределения партиций
```shell
docker exec -it kafka-0 kafka-topics --bootstrap-server localhost:9092 --command-config /etc/kafka/secrets/admin.properties --describe --topic balanced_topic
```

### Моделирование сбоя брокера
#### Остановка брокера kafka-1
```shell
docker stop kafka-1
```

#### Проверка состояния топиков
```shell
docker exec -it kafka-0 kafka-topics --bootstrap-server localhost:9092 --command-config /etc/kafka/secrets/admin.properties --describe --topic balanced_topic
```

#### Запуск брокера kafka-1
```shell
docker start kafka-1
```

#### Запуск выбора предпочтительного лидера для всех топиков кластера
```shell
docker exec -it kafka-0 kafka-leader-election --bootstrap-server localhost:9092 --admin.config /etc/kafka/secrets/admin.properties --election-type PREFERRED --all-topic-partitions
```

#### Проверка восстановления синхронизации
```shell
docker exec -it kafka-0 kafka-topics --bootstrap-server localhost:9092 --command-config /etc/kafka/secrets/admin.properties --describe --topic balanced_topic
```

### Настройка защищённого соединения и управление доступом
#### Проверка доступных топиков
```shell
docker exec -it kafka-0 kafka-topics --bootstrap-server localhost:9092 --command-config /etc/kafka/secrets/admin.properties --list
```

#### Проверка доступных ACL
```shell
docker exec -it kafka-0 kafka-acls --bootstrap-server localhost:9092 --command-config /etc/kafka/secrets/admin.properties --list
```

#### Проверка записи в topic-1
```shell
docker exec -it kafka-0 kafka-console-producer --bootstrap-server localhost:9092 --producer.config /etc/kafka/secrets/producer.properties --topic topic-1
```

#### Проверка чтения из topic-1
```shell
docker exec -it kafka-0 kafka-console-consumer --bootstrap-server localhost:9092 --consumer.config /etc/kafka/secrets/consumer.properties --topic topic-1 --group secure-processor-id --from-beginning --max-messages 1
```

#### Проверка записи в topic-2
```shell
docker exec -it kafka-0 kafka-console-producer --bootstrap-server localhost:9092 --producer.config /etc/kafka/secrets/producer.properties --topic topic-2
```

#### Проверка чтения из topic-2
```shell
docker exec -it kafka-0 kafka-console-consumer --bootstrap-server localhost:9092 --consumer.config /etc/kafka/secrets/consumer.properties --topic topic-2 --group secure-processor-id --from-beginning --max-messages 1
```