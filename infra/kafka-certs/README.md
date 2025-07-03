### Создайте сертификаты для каждого брокера
#### Создаем файл конфигурации для корневого сертификата (Root CA) ca.cnf

```text
[ policy_match ]
countryName = match
stateOrProvinceName = match
organizationName = match
organizationalUnitName = optional
commonName = supplied
emailAddress = optional

[ req ]
prompt = no
distinguished_name = dn
default_md = sha256
default_bits = 4096
x509_extensions = v3_ca

[ dn ]
countryName = RU
organizationName = Yandex
organizationalUnitName = Practice
localityName = Moscow
commonName = yandex-practice-kafka-ca

[ v3_ca ]
subjectKeyIdentifier = hash
basicConstraints = critical,CA:true
authorityKeyIdentifier = keyid:always,issuer:always
keyUsage = critical,keyCertSign,cRLSign
```

### Создаем корневой сертификат - Root CA (локальный терминал)

```shell
openssl req -new -nodes \
   -x509 \
   -days 365 \
   -newkey rsa:2048 \
   -keyout ca.key \
   -out ca.crt \
   -config ca.cnf
```

### Создаем файл для хранения сертификата безопасности ca.pem (локальный терминал)

```shell
cat ca.crt ca.key > ca.pem
```

### Создаем файлы конфигурации для каждого брокера
#### Для kafka-0 создаем файл kafka-0/kafka-0.cnf

```text
[req]
prompt = no
distinguished_name = dn
default_md = sha256
default_bits = 4096
req_extensions = v3_req

[ dn ]
countryName = RU
organizationName = Yandex
organizationalUnitName = Practice
localityName = Moscow
commonName = kafka-0

[ v3_ca ]
subjectKeyIdentifier = hash
basicConstraints = critical,CA:true
authorityKeyIdentifier = keyid:always,issuer:always
keyUsage = critical,keyCertSign,cRLSign

[ v3_req ]
subjectKeyIdentifier = hash
basicConstraints = CA:FALSE
nsComment = "OpenSSL Generated Certificate"
keyUsage = critical, digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = kafka-0
DNS.2 = kafka-0-external
DNS.3 = localhost
```

#### Для kafka-1 создаем файл kafka-1/kafka-1.cnf

```text
[req]
prompt = no
distinguished_name = dn
default_md = sha256
default_bits = 4096
req_extensions = v3_req

[ dn ]
countryName = RU
organizationName = Yandex
organizationalUnitName = Practice
localityName = Moscow
commonName = kafka-1

[ v3_ca ]
subjectKeyIdentifier = hash
basicConstraints = critical,CA:true
authorityKeyIdentifier = keyid:always,issuer:always
keyUsage = critical,keyCertSign,cRLSign

[ v3_req ]
subjectKeyIdentifier = hash
basicConstraints = CA:FALSE
nsComment = "OpenSSL Generated Certificate"
keyUsage = critical, digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = kafka-1
DNS.2 = kafka-1-external
DNS.3 = localhost
```

#### Для kafka-2 создаем файл kafka-2/kafka-2.cnf

```text
[req]
prompt = no
distinguished_name = dn
default_md = sha256
default_bits = 4096
req_extensions = v3_req

[ dn ]
countryName = RU
organizationName = Yandex
organizationalUnitName = Practice
localityName = Moscow
commonName = kafka-2

[ v3_ca ]
subjectKeyIdentifier = hash
basicConstraints = critical,CA:true
authorityKeyIdentifier = keyid:always,issuer:always
keyUsage = critical,keyCertSign,cRLSign

[ v3_req ]
subjectKeyIdentifier = hash
basicConstraints = CA:FALSE
nsComment = "OpenSSL Generated Certificate"
keyUsage = critical, digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth, clientAuth
subjectAltName = @alt_names

[ alt_names ]
DNS.1 = kafka-2
DNS.2 = kafka-2-external
DNS.3 = localhost
```

### Создаем приватные ключи и запросы на сертификат - CSR (локальный терминал)

```shell
openssl req -new \
    -newkey rsa:2048 \
    -keyout kafka-0/kafka-0.key \
    -out kafka-0/kafka-0.csr \
    -config kafka-0/kafka-0.cnf \
    -nodes

openssl req -new \
    -newkey rsa:2048 \
    -keyout kafka-1/kafka-1.key \
    -out kafka-1/kafka-1.csr \
    -config kafka-1/kafka-1.cnf \
    -nodes

openssl req -new \
    -newkey rsa:2048 \
    -keyout kafka-2/kafka-2.key \
    -out kafka-2/kafka-2.csr \
    -config kafka-2/kafka-2.cnf \
    -nodes
```

### Создаем сертификаты брокеров, подписанный CA (локальный терминал)

```shell
openssl x509 -req \
    -days 3650 \
    -in kafka-0/kafka-0.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out kafka-0/kafka-0.crt \
    -extfile kafka-0/kafka-0.cnf \
    -extensions v3_req

openssl x509 -req \
    -days 3650 \
    -in kafka-1/kafka-1.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out kafka-1/kafka-1.crt \
    -extfile kafka-1/kafka-1.cnf \
    -extensions v3_req

openssl x509 -req \
    -days 3650 \
    -in kafka-2/kafka-2.csr \
    -CA ca.crt \
    -CAkey ca.key \
    -CAcreateserial \
    -out kafka-2/kafka-2.crt \
    -extfile kafka-2/kafka-2.cnf \
    -extensions v3_req
```

### Создаем PKCS12-хранилища (локальный терминал)

```shell
openssl pkcs12 -export \
    -in kafka-0/kafka-0.crt \
    -inkey kafka-0/kafka-0.key \
    -chain \
    -CAfile ca.pem \
    -name kafka-0 \
    -out kafka-0/kafka-0.p12 \
    -password pass:password

openssl pkcs12 -export \
    -in kafka-1/kafka-1.crt \
    -inkey kafka-1/kafka-1.key \
    -chain \
    -CAfile ca.pem \
    -name kafka-1 \
    -out kafka-1/kafka-1.p12 \
    -password pass:password

openssl pkcs12 -export \
    -in kafka-2/kafka-2.crt \
    -inkey kafka-2/kafka-2.key \
    -chain \
    -CAfile ca.pem \
    -name kafka-2 \
    -out kafka-2/kafka-2.p12 \
    -password pass:password
```

### Создайте Truststore и Keystore для каждого брокера
#### Начнем с создания Keystore (локальный терминал)

```shell
keytool -importkeystore \
    -deststorepass password \
    -destkeystore kafka-0/kafka.kafka-0.keystore.pkcs12 \
    -srckeystore kafka-0/kafka-0.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass password

keytool -importkeystore \
    -deststorepass password \
    -destkeystore kafka-1/kafka.kafka-1.keystore.pkcs12 \
    -srckeystore kafka-1/kafka-1.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass password

keytool -importkeystore \
    -deststorepass password \
    -destkeystore kafka-2/kafka.kafka-2.keystore.pkcs12 \
    -srckeystore kafka-2/kafka-2.p12 \
    -deststoretype PKCS12  \
    -srcstoretype PKCS12 \
    -noprompt \
    -srcstorepass password
```

#### Создаем Truststore для Kafka (локальный терминал)

```shell
keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore kafka-0/kafka.kafka-0.truststore.jks \
    -storepass password \
    -noprompt

keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore kafka-1/kafka.kafka-1.truststore.jks \
    -storepass password \
    -noprompt

keytool -import \
    -file ca.crt \
    -alias ca \
    -keystore kafka-2/kafka.kafka-2.truststore.jks \
    -storepass password \
    -noprompt
```

### Создаем файлы с паролями, которые указывали в предыдущих командах (локальный терминал)

```shell
echo "password" > kafka-0/kafka-0_sslkey_creds
echo "password" > kafka-0/kafka-0_keystore_creds
echo "password" > kafka-0/kafka-0_truststore_creds

echo "password" > kafka-1/kafka-1_sslkey_creds
echo "password" > kafka-1/kafka-1_keystore_creds
echo "password" > kafka-1/kafka-1_truststore_creds

echo "password" > kafka-2/kafka-2_sslkey_creds
echo "password" > kafka-2/kafka-2_keystore_creds
echo "password" > kafka-2/kafka-2_truststore_creds
```

### Импортируем PKCS12 в JKS (локальный терминал)

```shell
keytool -importkeystore \
    -srckeystore kafka-0/kafka-0.p12 \
    -srcstoretype PKCS12 \
    -destkeystore kafka-0/kafka.keystore.jks \
    -deststoretype JKS \
    -deststorepass password

keytool -importkeystore \
    -srckeystore kafka-1/kafka-1.p12 \
    -srcstoretype PKCS12 \
    -destkeystore kafka-1/kafka.keystore.jks \
    -deststoretype JKS \
    -deststorepass password

keytool -importkeystore \
    -srckeystore kafka-2/kafka-2.p12 \
    -srcstoretype PKCS12 \
    -destkeystore kafka-2/kafka.keystore.jks \
    -deststoretype JKS \
    -deststorepass password
```

### Импортируем CA в Truststore (локальный терминал)

```shell
keytool -import -trustcacerts -file ca.crt \
    -keystore kafka-0/kafka.truststore.jks \
    -storepass password -noprompt -alias ca

keytool -import -trustcacerts -file ca.crt \
    -keystore kafka-1/kafka.truststore.jks \
    -storepass password -noprompt -alias ca

keytool -import -trustcacerts -file ca.crt \
    -keystore kafka-2/kafka.truststore.jks \
    -storepass password -noprompt -alias ca
```