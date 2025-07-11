package com.turinmachin.unilife.configuration;

import com.azure.ai.inference.ChatCompletionsAsyncClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIClientConfig {

    @Value("${ai.chat.endpoint}")
    private String endpoint;

    @Value("${ai.chat.api-key}")
    private String apiKey;

    @Value("${ai.chat.default-model}")
    private String defaultModel;

    @Bean
    public ChatCompletionsAsyncClient chatCompletionsClient() {
        return new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildAsyncClient();
    }

    @Bean
    public String defaultModel() {
        return defaultModel;
    }
}
