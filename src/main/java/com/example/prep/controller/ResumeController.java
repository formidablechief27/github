package com.example.prep.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ResumeController {
	
	@PostMapping("/resume-questions")
    public ResponseEntity<?> uploadResume(@RequestParam("resume") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        try {
            String resumeText = extractTextFromPDF(file.getInputStream());
            if (resumeText.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Failed to extract resume text.");
            }

            String prompt = "Based on the following resume, generate 15 balanced interview questions on the user's skills, projects, and achievements, please dont give any titles, just the questions:\n"
                            + resumeText;

            String payload = buildPayload(prompt);

            String apiResponse = sendApiRequest(payload);
            System.out.println(apiResponse);
            List<LinkedHashMap<String, Object>> questions = parseQuestions(apiResponse);
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
            map.put("status", "OK");
            map.put("data", datamap);
            datamap.put("questions", questions);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }
	
	@PostMapping("/follow-up-question")
    public ResponseEntity<?> pp1(
            @RequestParam("followup-id") String fid,
            @RequestParam("question-id") String qid,
            @RequestParam("answer") String ans) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
        map.put("status", "OK");
        LinkedHashMap<String, Object> question = new LinkedHashMap<>();
        question.put("question-id", qid);
        question.put("followup-id", Math.max(1, Integer.parseInt(fid) + 1));
        question.put("description", "This is a sample follow up question");
        datamap.put("question", question);
        map.put("data", datamap);
        return ResponseEntity.ok(map);
    }
	 
	private String extractTextFromPDF(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private static String buildPayload(String prompt) {
        String escapedPrompt = escapeJson(prompt);
        String payload = "{\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}\n" +
                "  ],\n" +
                "  \"model\": \"llama-3.3-70b-versatile\",\n" +
                "  \"temperature\": 1,\n" +
                "  \"max_completion_tokens\": 1024,\n" +
                "  \"top_p\": 1,\n" +
                "  \"stream\": false,\n" +
                "  \"stop\": null\n" +
                "}";
        return payload;
    }
    
    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
    
    private static String sendApiRequest(String payload) {
        String apiUrl = "https://api.groq.com/openai/v1/chat/completions";
        String apiKey = "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"; // Replace with your actual Groq API key
        
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            InputStream is = (responseCode == HttpURLConnection.HTTP_OK)
                                ? connection.getInputStream() : connection.getErrorStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private static List<LinkedHashMap<String, Object>> parseQuestions(String apiResponse) {
        List<LinkedHashMap<String, Object>> questions = new ArrayList<>();
        int ind = -1;
        String str = "";
        String find = "1.";
        for(int i=0;i<apiResponse.length();i++) {
        	if(i + find.length() > apiResponse.length()) break;
        	if(apiResponse.substring(i, i + find.length()).equals(find)) {
        		int idx = i + find.length() + 1;
        		str = "";
        		while(true) {
        			str += apiResponse.charAt(idx);
        			if(apiResponse.charAt(idx) == '?') break;
        			idx++;
        		}
        		LinkedHashMap<String, Object> pp = new LinkedHashMap<>();
        		int num = 0;
        		for(int j=0;j<find.length();j++) {
        			char ch = find.charAt(j);
        			if(ch >= 48 && ch <= 57) {
        				num *= 10;
        				num += (ch - 48);
        			}
        		}
        		pp.put("question-id", num);
        		pp.put("description", str);
        		questions.add(pp);
        		i = idx;
        		if(find.equals("1.")) find = "2.";
        		else if(find.equals("2.")) find = "3.";
        		else if(find.equals("3.")) find = "4.";
        		else if(find.equals("4.")) find = "5.";
        		else if(find.equals("5.")) find = "6.";
        		else if(find.equals("6.")) find = "7.";
        		else if(find.equals("7.")) find = "8.";
        		else if(find.equals("8.")) find = "9.";
        		else if(find.equals("9.")) find = "10.";
        		else if(find.equals("10.")) find = "11.";
        		else if(find.equals("11.")) find = "12.";
        		else if(find.equals("12.")) find = "13.";
        		else if(find.equals("13.")) find = "14.";
        		else if(find.equals("14.")) find = "15.";
        		else break;
        	}
        }
        System.out.println(questions);
        return questions;
    }
}
