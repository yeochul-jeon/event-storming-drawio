package com.eventstorming.drawio.config;

import org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatAutoConfiguration;
import org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class AiProviderConfig {

    @Configuration
    @ConditionalOnProperty(name = "app.ai.provider", havingValue = "claude", matchIfMissing = true)
    @Import(AnthropicChatAutoConfiguration.class)
    static class ClaudeConfig {
    }

    @Configuration
    @ConditionalOnProperty(name = "app.ai.provider", havingValue = "gemini")
    @Import(GoogleGenAiChatAutoConfiguration.class)
    static class GeminiConfig {
    }
}
