package com.example.prep;
import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import com.assemblyai.api.resources.transcripts.types.TranscriptStatus;
import com.assemblyai.api.resources.files.types.UploadedFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AssemblyAITranscription {

    public static void main(String[] args) throws IOException {
        // Initialize AssemblyAI client
        AssemblyAI client = AssemblyAI.builder()
                .apiKey("a46ff04242af42c59017fdda83e06896") // Replace with your actual API key
                .build();

        // Step 1: Read the local WebM file into a byte array
        byte[] audioData = Files.readAllBytes(Paths.get("C:\\Users\\Dhrumil\\Downloads\\ee.webm")); // Replace with your actual file path

        // Step 2: Upload the byte array to AssemblyAI and get the UploadedFile object
        UploadedFile uploadedFile = client.files().upload(audioData);

        // Step 3: Extract the file URL from UploadedFile
        String uploadedFileUrl = uploadedFile.getUploadUrl();
        System.out.println("Uploaded file URL: " + uploadedFileUrl);

        // Step 4: Request transcription using the uploaded file URL
        Transcript transcript = client.transcripts().transcribe(uploadedFileUrl);

        if (transcript.getStatus() == TranscriptStatus.ERROR) {
            throw new RuntimeException("Transcription failed: " + transcript.getError().orElse("Unknown error"));
        }

        // Step 5: Print the transcript
        System.out.println("Transcript: " + transcript.getText());
    }
}
