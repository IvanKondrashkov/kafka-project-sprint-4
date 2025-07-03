package ru.practicum.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.apache.kafka.common.serialization.StringSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    private final SslBundles sslBundles;
    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = kafkaProperties.buildProducerProperties(sslBundles);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}