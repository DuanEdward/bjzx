package com.hebei.cs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Jackson配置
 *
 * @author Hebei CS
 * @since 2024-12-15
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 禁用将日期写为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 自定义LocalDateTime反序列化器
        CustomLocalDateTimeDeserializer deserializer = new CustomLocalDateTimeDeserializer();

        // 配置LocalDateTime的序列化和反序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, deserializer);

        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    /**
     * 自定义LocalDateTime反序列化器，支持多种日期格式
     */
    public static class CustomLocalDateTimeDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            String dateStr = p.getText().trim();

            if (dateStr == null || dateStr.isEmpty()) {
                return null;
            }

            // 尝试解析各种格式
            try {
                // 如果只有日期部分 (yyyy-MM-dd)
                if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    LocalDate date = LocalDate.parse(dateStr);
                    return date.atStartOfDay();
                }

                // 如果是标准日期时间格式
                if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}$")) {
                    return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }

                // 尝试其他常见格式
                DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                };

                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return LocalDateTime.parse(dateStr, formatter);
                    } catch (Exception e) {
                        // 继续尝试下一个格式
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("Failed to parse date: " + dateStr, e);
            }

            throw new RuntimeException("Unrecognizable date format: " + dateStr);
        }
    }
}