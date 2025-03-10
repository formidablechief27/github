package com.example.prep;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class LaafficFileUploader {

    // Replace with your actual credentials
    private static final String API_KEY = "TMieqKQ4";  // App Management - appId
    private static final String API_SECRET = "YBdFaH6w";  // App Management - secret
    private static final String BASE_URL = "https://api.laaffic.com/v3/voice/fileUpload";

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Dhrumil\\Downloads\\ee.webm"; // Provide the correct file path
        fileUpload(filePath);
    }

    public static void fileUpload(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Error: File not found at path: " + filePath);
            return;
        }

        try {
            String fileName = file.getName();
            String fileBase64 = fileToBase64(file);

            // Generate MD5 signature
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String sign = SecureUtil.md5((API_KEY + API_SECRET + timestamp).toLowerCase());

            // Debugging prints (remove in production)
            System.out.println("Timestamp: " + timestamp);
            System.out.println("Generated Sign: " + sign);
            System.out.println("File Base64 Length: " + fileBase64.length());

            // Create JSON request body
            JSONObject bodyJson = JSONUtil.createObj()
                    .set("fileName", fileName)
                    .set("file", fileBase64); // Some APIs use "data" instead of "file"

            HttpRequest request = HttpRequest.post(BASE_URL)
                    .header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                    .header("Sign", sign)
                    .header("Timestamp", timestamp)
                    .header("Api-Key", API_KEY)
                    .body(bodyJson.toString());

            HttpResponse response = request.execute();

            // Handle response
            System.out.println("Response Status: " + response.getStatus());
            System.out.println("Response Body: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error uploading file.");
        }
    }

    private static String fileToBase64(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = fis.readAllBytes();
            return Base64.encode(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
