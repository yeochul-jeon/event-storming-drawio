package com.eventstorming.drawio;

import org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatAutoConfiguration;
import org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration;
import org.springframework.ai.model.google.genai.autoconfigure.embedding.GoogleGenAiEmbeddingConnectionAutoConfiguration;
import org.springframework.ai.model.google.genai.autoconfigure.embedding.GoogleGenAiTextEmbeddingAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        AnthropicChatAutoConfiguration.class,
        GoogleGenAiChatAutoConfiguration.class,
        GoogleGenAiEmbeddingConnectionAutoConfiguration.class,
        GoogleGenAiTextEmbeddingAutoConfiguration.class
})
public class EventStormingDrawioApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventStormingDrawioApplication.class, args);
    }
}
