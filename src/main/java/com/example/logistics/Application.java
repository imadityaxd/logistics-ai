package com.example.logistics;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostMapping("/analyze")
    public Map<String, String> analyze(@RequestBody Map<String, String> body) {
        String news = body.getOrDefault("news", "");
        
        // 1. AUTO-DETECT PROJECT ID (Safer than hardcoding)
        String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
        if (projectId == null || projectId.isEmpty()) {
            projectId = "logistics-ai-494711"; // Your fallback
        }

        try (VertexAI v = new VertexAI(projectId, "us-central1")) {
            // 2. USE STABLE 2.5 FLASH MODEL
            GenerativeModel model = new GenerativeModel("gemini-2.5-flash", v);
            
            String prompt = "Supply Chain Expert: Analyze this disruption and provide 3 alternate routes. Disruption: " + news;
            var response = model.generateContent(prompt);
            
            return Map.of("result", ResponseHandler.getText(response));
        } catch (Exception e) {
            // 3. SHOW THE ACTUAL ERROR ON SCREEN
            return Map.of("result", "!!! BACKEND ERROR: " + e.getMessage());
        }
    }
}