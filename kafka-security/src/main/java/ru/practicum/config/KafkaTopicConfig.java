package ru.practicum.config;

import java.util.Map;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.dto.Topics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {
    private final SslBundles sslBundles;
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.putAll(kafkaProperties.buildAdminProperties(sslBundles));
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic balanced() {
        return TopicBuilder.name(Topics.BALANCED_TOPIC)
                .partitions(8)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic one() {
        return TopicBuilder.name(Topics.ONE_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }

    @Bean
    public NewTopic two() {
        return TopicBuilder.name(Topics.TWO_TOPIC)
                .partitions(3)
                .replicas(2)
                .build();
    }
}