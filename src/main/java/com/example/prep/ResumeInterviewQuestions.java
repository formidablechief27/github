package com.example.prep;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ResumeInterviewQuestions {

    public static void main(String[] args) {
        String pdfFilePath = "C:\\Users\\Dhrumil\\OneDrive\\Desktop\\pr\\PrepWell\\src\\main\\java\\com\\example\\prep\\resume.pdf";
        
        // Extract text from the PDF resume
        String resumeText = extractTextFromPDF(pdfFilePath);
        if (resumeText.isEmpty()) {
            System.err.println("Failed to extract resume text.");
            return;
        }
        
        // Build the prompt with the resume text
        String prompt = "Based on the following resume, generate 15 balanced interview questions on the user's skills, projects, and achievements:\n" 
                        + resumeText;
        
        // Build the JSON payload for the API call
        String payload = buildPayload(prompt);
        
        // Send the request to Groq AI and get the API response
        String apiResponse = sendApiRequest(payload);
        
        // Print the full API response
        System.out.println("API Response:");
        System.out.println(apiResponse);
        
        // Extract questions from the API response into a list
        List<String> questions = parseQuestions(apiResponse);
        
        // Print the extracted questions
        System.out.println("\nExtracted Questions:");
        for (String question : questions) {
            System.out.println(question);
        }
    }
    
    // Extracts text from a PDF file using Apache PDFBox
    private static String extractTextFromPDF(String filePath) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();
            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    // Builds the JSON payload with the provided prompt
    private static String buildPayload(String prompt) {
        // Escape special JSON characters in the prompt
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
    
    // Escapes JSON special characters in a string
    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
    
    // Sends the API request to Groq AI and returns the response as a string
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
    
    // Parses the API response and extracts questions
    // Assumes that each question is on a new line starting with a number followed by a period
    private static List<String> parseQuestions(String apiResponse) {
        List<String> questions = new ArrayList<>();
        int ind = -1;
        String str = "";
        String find = "1.";
        for(int i=0;i<apiResponse.length()-1;i++) {
        	if(apiResponse.substring(i, i + find.length()).equals(find)) {
        		int idx = i + find.length() + 1;
        		str = "";
        		while(true) {
        			str += apiResponse.charAt(idx);
        			if(apiResponse.charAt(idx) == '?') break;
        			idx++;
        		}
        		questions.add(str);
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
        return questions;
    }
}
