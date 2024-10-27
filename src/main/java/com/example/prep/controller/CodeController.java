package com.example.prep.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLDecoder;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.prep.model.Questions;
import com.example.prep.model.Testcases;
import com.example.prep.repository.QuestionRepository;
import com.example.prep.repository.TestcasesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class CodeController {
	
	@Autowired
	QuestionRepository repo;
	TestcasesRepository t_repo;
	
	@Autowired
	CodeController(QuestionRepository repo, TestcasesRepository t_repo) {
		this.repo = repo;
		this.t_repo = t_repo;
	}

	@PostMapping("/run_code")
	public ResponseEntity<String> questionSearch(@RequestBody Map<String, Object> requestBody) {
		ArrayList<String> inputs = (ArrayList<String>) requestBody.get("inputs");
		String language = (String) requestBody.get("language");
		String code = (String) requestBody.get("code");
		try {code = URLDecoder.decode(code, "UTF-8");}
		catch (UnsupportedEncodingException e) {e.printStackTrace();}
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
	    responseMap.put("status", "OK");
	    
	    ArrayList<String> outputs = new ArrayList<>();
	    for(String input : inputs) {
	    	if(language.equals("Java")) {
		    	String output = run(code, input);
		    	outputs.add(output);
		    }
	    }
	    responseMap.put("results", outputs);
	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }
	}
	
	@PostMapping("/submit_code")
	public ResponseEntity<String> submit(@RequestBody Map<String, Object> requestBody) {
		String language = (String) requestBody.get("language");
		String code = (String) requestBody.get("code");
		int id = ((Integer) requestBody.get("id"));
		try {code = URLDecoder.decode(code, "UTF-8");}
		catch (UnsupportedEncodingException e) {e.printStackTrace();}
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
	    responseMap.put("status", "OK");
	    Optional<Questions> ques = repo.findById(id);
	    List<Testcases> list = t_repo.findAll();
	    List<Testcases> flist = new ArrayList<>();
	    TreeMap<Integer, Testcases> map = new TreeMap<>();
	    for(Testcases t : list) if(t.getId() >= ques.get().getTestcaseStart() && t.getId() <= ques.get().getTestcaseEnd()) flist.add(t);
	    for(Testcases t : flist) map.put(t.getId(), t);
	    ArrayList<String> outputs = new ArrayList<>();
	    for(Map.Entry<Integer, Testcases> entry : map.entrySet()) {
	    	String output = "";
	    	if(language.equals("Java")) {
	    		output = run(code, entry.getValue().getInput());
	    	}
	    	System.out.println(output.trim() + " " + entry.getValue().getOutput().trim());
	    	if(output.trim().equalsIgnoreCase(entry.getValue().getOutput().trim())) outputs.add("Accepted");
    		else if(output.trim().contains("Time Limit Exceeded")) outputs.add("Time Limit Exceeded");
    		else outputs.add("Wrong Answer");
	    }
	    responseMap.put("results", outputs);
	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }
	}
	
	public String extract(String code) {
	   String className = null;
       Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
       Matcher matcher = pattern.matcher(code);
       if (matcher.find()) className = matcher.group(1);
       else className = "Main";
       return className;
	}
	
	public String run(String code, String input) {
		String filename = extract(code);
		String og_class = filename;
		File sourceFile = new File(filename + ".java");
        FileWriter writer;
		try {
			writer = new FileWriter(sourceFile);
			writer.write(code);
	        writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			sourceFile.delete();
			e1.printStackTrace();
		}
		ExecutorService executor = Executors.newSingleThreadExecutor();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult = compiler.run(null, null, null, sourceFile.getPath());
        // Use a Future to track the execution
        Future<String> future = executor.submit(() -> {
            // Compile the Java source file
        	String output = "";
			if (compilationResult == 0) {
			    // Compilation succeeded, execute the compiled class
			    InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
			    String className = sourceFile.getName().replace(".java", "");
			    ProcessBuilder processBuilder = new ProcessBuilder("java", "-Xint", className);
			    processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
			    processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
			    Process process = processBuilder.start();

                // Write the input to the standard input of the process
                try (OutputStream outputStream = process.getOutputStream()) {
                    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(inputBytes);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
			    while ((line = reader.readLine()) != null) output += line + "\n";
			    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			    while ((line = reader.readLine()) != null) output += line + "\n";
			    return output;
			} else {
				try {
		            ProcessBuilder processBuilder = new ProcessBuilder("javac", sourceFile.getPath());
		            processBuilder.redirectError(ProcessBuilder.Redirect.PIPE);
		            Process compilationProcess = processBuilder.start();
		            // Read the error stream and append to output
		            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(compilationProcess.getErrorStream()))) {
		                String line;
		                while ((line = errorReader.readLine()) != null) output += line + "\n";
		            }
		            return output;
		        } catch (IOException e) {
		            e.printStackTrace(); // Handle exceptions as needed
		            return output += "Error during compilation";
		        }
			}
        });
        try {
            String result = future.get(5, TimeUnit.SECONDS); 
            sourceFile.delete();
            return result;
        } catch (Exception e) {
        	e.printStackTrace();
        	sourceFile.delete();
            future.cancel(true);
            return "Time Limit Exceeded";
        }
	}
	
}
