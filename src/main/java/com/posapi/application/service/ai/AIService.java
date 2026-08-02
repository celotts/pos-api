package com.posapi.application.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient.Builder chatClientBuilder;

    public String generateProductDescription(String productName, String characteristics) {
        ChatClient chatClient = chatClientBuilder.build();

        // Define un prompt para la IA
        PromptTemplate promptTemplate = new PromptTemplate("""
            Eres un asistente de marketing para una tienda POS.
            Genera una descripción de producto atractiva y concisa para el siguiente producto.
            Incluye sus características clave y beneficios para el cliente.
            Producto: {productName}
            Características: {characteristics}
            La descripción debe tener un máximo de 50 palabras.
            """);

        // Crea el prompt con los datos del producto
        String prompt = promptTemplate.render(
                java.util.Map.of("productName", productName, "characteristics", characteristics)
        );

        // Envía el prompt a la IA y obtiene la respuesta
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
