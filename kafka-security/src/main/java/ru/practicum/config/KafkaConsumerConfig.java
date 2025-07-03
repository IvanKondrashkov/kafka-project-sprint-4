package ru.practicum.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final SslBundles sslBundles;
    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = kafkaProperties.buildConsumerProperties(sslBundles);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}