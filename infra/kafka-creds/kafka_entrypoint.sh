#!/bin/bash
set -e

kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/admin.properties \
  --add Write, Describe \
  --allow-principal User:producer \
  --producer \
  --topic topic-1

kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/admin.properties \
  --add Write, Describe \
  --allow-principal User:producer \
  --producer \
  --topic topic-2

kafka-acls --bootstrap-server kafka-0:9092 \
  --command-config /etc/kafka/secrets/admin.properties \
  --add Read, Describe \
  --allow-principal User:consumer \
  --consumer \
  --topic topic-1 \
  --group secure-processor-id

exec "$@"