package com.example.prep.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Random;
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
	    System.out.println(code);
	    System.out.println(language);
	    System.out.println(inputs);
	    ArrayList<String> outputs = new ArrayList<>();
	    for(String input : inputs) {
	    	if(language.equalsIgnoreCase("Java")) {
		    	String output = run(code, input);
		    	outputs.add(output);
		    }
	    	else if(language.equalsIgnoreCase("cpp")) {
	    		String output = run_cpp(code, input).toString();
	    		outputs.add(output);
	    	}
	    	else {
	    		String output = runpy(code, input).toString();
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
		String p = (String) requestBody.get("id");
		int id = Integer.parseInt(p);
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
	    int count[] = new int[3];
	    int lt = -1, ll = -1;
	    HashMap<String, ArrayList<Integer>> rmap = new HashMap<>();
	    int ctr = 1;
	    for(Map.Entry<Integer, Testcases> entry : map.entrySet()) {
	    	String output = "";
	    	if(language.equals("Java")) {
	    		output = run(code, entry.getValue().getInput());
	    	}
	    	else if(language.equals("Cpp")) {
	    		output = run_cpp(code, entry.getValue().getInput()).toString();
	    	}
	    	else {
	    		output = runpy(code, entry.getValue().getInput()).toString();
	    	}
	    	System.out.println(output.trim() + " " + entry.getValue().getOutput().trim());
	    	String res = "";
	    	if(output.trim().equalsIgnoreCase(entry.getValue().getOutput().trim())) res = ("Accepted");
    		else if(output.trim().contains("Time Limit Exceeded")) res = ("Time Limit Exceeded");
    		else res = ("Wrong Answer");
	    	System.out.println(res);
	    	if(res.equals("Accepted")) count[0]++;
	    	if(res.equals("Time Limit Exceeded")) {
	    		count[1]++;
	    		if(lt == -1) lt = ctr;
	    	}
	    	if(res.equals("Wrong Answer")) {
	    		count[2]++;
	    		if(ll == -1) ll = ctr;
	    	}
	    	ctr++;
	    }
	    String result = "";
	    int min = 100;
	    if(lt != -1) min = lt;
	    if(ll != -1) min = ll;
	    if(min == 100) result = "Accepted";
	    else if(min == ll) result = "Wrong Answer on Test " + ll;
	    else result = "Time Limit Exceeded on Test" + lt;
	    responseMap.put("result", result);
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
	
	public String injectFileRedirect(String cppCode, String filename) {
        int mainIndex = cppCode.indexOf("main(");
        if (mainIndex == -1) {
        	mainIndex = cppCode.indexOf("(", cppCode.indexOf("main"));
        }
        int mainEndIndex = cppCode.indexOf("{", mainIndex);
        if (mainEndIndex == -1) {
            return cppCode;
        }
        StringBuilder modifiedCode = new StringBuilder(cppCode);
        modifiedCode.insert(mainEndIndex + 1, "freopen(\"" + filename + "\", \"r\", stdin);");
        return modifiedCode.toString();
    }
	
	public StringBuilder run_cpp(String f_code, String input) {
		Random rand = new Random();
		int num = rand.nextInt(1000);
		String fname = "main" + num + ".cpp";
    	File sourceFile = new File(fname);
    	f_code = "#include <cstdio> \n" + f_code;
    	f_code = injectFileRedirect(f_code, "pooja" + num + ".txt");
    	//System.out.println(f_code);
    	StringBuilder output = new StringBuilder();
        FileWriter writer;
        try {
            writer = new FileWriter(sourceFile);
            writer.write(f_code);
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ProcessBuilder processBuilder = new ProcessBuilder("g++", "-O0", "-m64", sourceFile.getPath(), "-o", "output" + num);
        Process compileProcess;
        try {
            compileProcess = processBuilder.start();
            // Capture compilation errors
            try (BufferedReader compileErrorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()))) {
                String line;
                while ((line = compileErrorReader.readLine()) != null) {
                	output.append(line + "\n");
                }
            }

            int compileExitCode = compileProcess.waitFor();

            if (compileExitCode != 0) {
                // Compilation error occurred
            	sourceFile.delete();
            	return output;
            }

        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
            sourceFile.delete();
            output.append("Execution Failed, Try Again");
            return output;
        }
       try {
    	    try (BufferedWriter writer_again = new BufferedWriter(new FileWriter("pooja" + num + ".txt"))) {
	            writer_again.write(input);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			ProcessBuilder processbuilder = new ProcessBuilder("./output" + num);
			processbuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
			processbuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Process process = processbuilder.start();
			
           Future<Boolean> future = executor.submit(() -> {
   			
   			try {
   			    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
   			    long end = System.currentTimeMillis();
   			    String line;
   			    long start1 = System.currentTimeMillis();
   			    while ((line = reader.readLine()) != null) {
   			    	output.append(line + "\n");
   			    }
   			    end = System.currentTimeMillis();
   			    long time = end - start1;
   			    System.out.println((end - start1) + "ms");
   			    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
   			    while ((line = reader.readLine()) != null) {
   			    	output.append(line + "\n");
   			    	if(line.trim().length() > 0) return false;
   		        }
   			    return true;
   			} catch (IOException e) {
   			    e.printStackTrace();
   			    return false;
   			}
           });

           try {
               boolean result = future.get(5, TimeUnit.SECONDS); // 5 seconds timeout
               sourceFile.delete();
               String outputFileName = "output" + num + ".exe";
               File outputFile = new File(outputFileName);
               outputFile.delete();
               String fileName = "pooja" + num + ".txt";
        	   File cookie = new File(fileName);
        	   if (cookie.exists()) cookie.delete();
        	   return output;
           } catch (Exception e) {
        	   String fileName = "pooja" + num + ".txt";
        	   File cookie = new File(fileName);
        	   if (cookie.exists()) cookie.delete();
        	   sourceFile.delete();
               String outputFileName = "output" + num + ".exe";
               File outputFile = new File(outputFileName);
               outputFile.delete();
               future.cancel(true);
               //System.out.println("TLE ");
               output.append("Time Limit Exceeded");
               return output;
           }
       }
       catch(Exception e) {
    	   output.append("Execution Failed, Try Again");
    	   return output;
       }
	}
	
	public StringBuilder runpy(String code, String input) {
        ArrayList<Long> times = new ArrayList<>();
        // Write the Python code to a file
        Random rand = new Random();
        int num = rand.nextInt(1000);
        String file_name = "main" + num + ".py";
        File sourceFile = new File(file_name);
        
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write(code);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder output = new StringBuilder();
        Future<StringBuilder> future = executor.submit(() -> {
            // Run Python code
            ProcessBuilder processBuilder = new ProcessBuilder("python3", sourceFile.getPath());
            processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

            try {
                Process process = processBuilder.start();

                // Write the input to the standard input of the process
                try (OutputStream outputStream = process.getOutputStream()) {
                    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(inputBytes);
                }

                // Capture the output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                int i = 0;
			    boolean flag = true;
			    String foutput = "";
			    long start = System.currentTimeMillis();
			    while ((line = reader.readLine()) != null) {
			    	output.append(line);
			    	output.append("\n");
	            }
			    long end = System.currentTimeMillis();
			    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			    while ((line = reader.readLine()) != null) {
			    	output.append(line);
			    	output.append("\n");
	            }
			    long time = end - start;
			    return output;
            } catch (IOException e) {
                e.printStackTrace();
                output.append("Python Error");
                return output;
            }
        });
        try {
            StringBuilder result = future.get(5, TimeUnit.SECONDS); // 5 seconds timeout
            sourceFile.delete();
            return result;
        } catch (Exception e) {
        	sourceFile.delete();
            future.cancel(true);
            output.append("Python Limit Exceeded");
            return output;
        }
    }
	
}
