package com.example.dossier.config;

import com.example.dossier.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, EmailMessage> consumerFactory(
            KafkaProperties kafkaProperties) {
        // Используем новый метод для получения свойств
        Map<String, Object> consumerProps = kafkaProperties.buildConsumerProperties(null);

        // Настраиваем десериализацию
        JsonDeserializer<EmailMessage> jsonDeserializer = new JsonDeserializer<>(EmailMessage.class);
        jsonDeserializer.addTrustedPackages("com.example.dossier.dto");

        return new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(), // Десериализатор для ключей
                jsonDeserializer          // Десериализатор для значений
        );
    }
}
