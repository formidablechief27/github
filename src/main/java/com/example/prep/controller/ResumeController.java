package com.example.prep.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.prep.model.Interview;
import com.example.prep.repository.DataCache;
import com.example.prep.repository.InterviewRepository;

@Controller
public class ResumeController {
	
	@Autowired
	InterviewRepository i;
	
	@Autowired
	ResumeController(InterviewRepository i) {
		this.i = i;
	}
	
	/*
	 * 	topic.add("Software Development and Frameworks");
	    topic.add("Databases And Optimization");
	    topic.add("Devops And Deployment");
	    topic.add("Problem Solvings");
	    topic.add("Project Design");
	    topic.add("Troubleshooting");
	 */
	
	String t[] = {"Software Development and Frameworks", "Databases And Optimization", "Devops And Deployment", "Problem Solvings", "Project Design", "Troubleshooting"};
	
	@PostMapping("/interview-stats")
	public ResponseEntity<?> interview_stats() {
		try {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            List<Interview> idata = i.findAll();
            List<LinkedHashMap<String, Object>> data = new ArrayList<>();
            for(Interview ii : idata) {
            	LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
            	String str = ii.getStrengths();
            	if(str.length() < 4) continue;
            	ArrayList<LinkedHashMap<String, Object>> arr = new ArrayList<>();
            	ArrayList<String> strengths = new ArrayList<>();
            	int id1 = 0;
            	int id2 = str.indexOf("$");
            	String s1 = str.substring(id1, id2);
            	id1 = id2 + 1;
            	id2 = str.indexOf("$", id1);
            	String s2 = str.substring(id1, id2);
            	id1 = id2 + 1;
            	id2 = str.indexOf("$", id1);
            	String s3 = str.substring(id1, id2);
            	strengths.add(s1);
            	strengths.add(s2);
            	strengths.add(s3);
            	String wk = ii.getWeaknesses();
            	ArrayList<String> weaknesses = new ArrayList<>();
            	id1 = 0;
            	id2 = wk.indexOf("$");
            	String s4 = wk.substring(id1, id2);
            	id1 = id2 + 1;
            	id2 = wk.indexOf("$", id1);
            	String s5 = wk.substring(id1, id2);
            	id1 = id2 + 1;
            	id2 = wk.indexOf("$", id1);
            	String s6 = wk.substring(id1, id2);
            	weaknesses.add(s4);
            	weaknesses.add(s5);
            	weaknesses.add(s6);
            	String scr = ii.getScore();
            	int cnt[] = new int[7];
            	int scores[] = new int[7];
            	int prev = -1;
            	for(int j=1;j<=6;j++) {
            		id1 = prev + 1;
            		id2 = scr.indexOf("$", id1);
            		int v = Integer.parseInt(scr.substring(id1, id2));
            		scores[j] = v;
            		id1 = id2 + 1;
            		id2 = scr.indexOf("$", id1);
            		int s = Integer.parseInt(scr.substring(id1, id2));
            		cnt[j] = s;
            		prev = id2;
            	}
            	String ffans = ii.getAnalysis();
            	prev = -1;
            	while(true) {
            		LinkedHashMap<String, Object> current = new LinkedHashMap<>();
            		id1 = prev + 1;
            		id2 = ffans.indexOf("$", id1);
            		if(id2 == -1) break;
            		if(id2 < id1) break;
            		String q = ffans.substring(id1, id2);
            		if(q.length() < 2) break;
            		id1 = id2 + 1;
            		id2 = ffans.indexOf("$", id1);
            		String s = ffans.substring(id1, id2);
            		id1 = id2 + 1;
            		id2 = ffans.indexOf("$", id1);
            		String im = ffans.substring(id1, id2);
            		id1 = id2 + 1;
            		id2 = ffans.indexOf("$", id1);
            		String tt = ffans.substring(id1, id2);
            		current.put("question", q);
    	    	    current.put("topic", s);
    	    	    current.put("score", im);
    	    	    current.put("improvement", tt);
    	    	    prev = id2;
    	    	    arr.add(current);
            	}
            	LinkedHashMap<String, Integer> dd = new LinkedHashMap<>();
        	    if(cnt[1] > 0) dd.put("Software Development and Frameworks", scores[1]);
        	    if(cnt[2] > 0) dd.put("Databases And Optimization", scores[2]);
        	    if(cnt[3] > 0) dd.put("Devops And Deployment", scores[3]);
        	    if(cnt[4] > 0) dd.put("Problem Solvings", scores[4]);
        	    if(cnt[5] > 0) dd.put("Project Design", scores[5]);
        	    if(cnt[6] > 0) dd.put("Troubleshooting", scores[6]);
        	    datamap.put("interview-id", ii.getId());
        	    datamap.put("scores", dd);
        	    datamap.put("strengths", strengths);
        	    datamap.put("weaknesses", weaknesses);
        	    datamap.put("analysis", arr);
        	    data.add(datamap);
            }
            map.put("status", "OK");
            map.put("data", data);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
	}
	
	@PostMapping("/evaluate-oa")
	public ResponseEntity<?> evaluate(@RequestBody Map<String, Object> requestBody) {
		try {
			List<Integer> aptitude = (List<Integer>) requestBody.get("aptitude");
			List<Integer> technical = (List<Integer>) requestBody.get("technical");
			List<Integer> english = (List<Integer>) requestBody.get("english");
			List<Integer> core = (List<Integer>) requestBody.get("core");
			int score = 0;
			
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
            map.put("status", "OK");
            map.put("data", datamap);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
	}
	
	@PostMapping("/hr-questions")
	public ResponseEntity<?> hr(@RequestParam("company") String company) {
		try {
			String prompt = "Generate 10 HR Round questions and company questions for the company " + company + ", please dont give any titles, just the questions. Give in the format 1. Question 1 2. Question 2 .... and so on.. Please add a ? at the end of every question";
			String payload = buildPayload(prompt);
			Interview ii = new Interview("", "", "", "");
            i.save(ii);
            int id = ii.getId();
            String apiResponse = sendApiRequest(payload);
            System.out.println(apiResponse);
            List<LinkedHashMap<String, Object>> questions = parseQuestions(apiResponse);
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
            map.put("status", "OK");
            datamap.put("interview-id", id);
            datamap.put("questions", questions);
            map.put("data", datamap);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
	}
	
	@PostMapping("/custom-interview")
	public ResponseEntity<?> custom(@RequestBody Map<String, Object> requestBody) {
		try {
			List<String> topics = (List<String>) requestBody.get("topics");
			String ftopics = "";
			for(String s : topics) ftopics += topics + ",";
			String difficulty = (String) requestBody.get("difficulty");
			int count = Integer.parseInt((String)requestBody.get("count"));
			String prompt = "Based on the following topics " + ftopics + " , generate" + count + " balanced interview questions for difficulty " + difficulty + " please dont give any titles, just the questions. Give in the format 1. Question 1 2. Question 2 .... and so on.. Please only generate " + count + "questions and add a ? at the end of every question";
					String payload = buildPayload(prompt);
			Interview ii = new Interview("", "", "", "");
            i.save(ii);
            int id = ii.getId();
            String apiResponse = sendApiRequest(payload);
            System.out.println(apiResponse);
            List<LinkedHashMap<String, Object>> questions = parseQuestions(apiResponse, count);
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
            map.put("status", "OK");
            datamap.put("interview-id", id);
            datamap.put("questions", questions);
            map.put("data", datamap);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
	}
	
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
            Interview ii = new Interview("", "", "", "");
            i.save(ii);
            int id = ii.getId();
            String apiResponse = sendApiRequest(payload);
            System.out.println(apiResponse);
            List<LinkedHashMap<String, Object>> questions = parseQuestions(apiResponse);
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
            map.put("status", "OK");
            datamap.put("interview-id", id);
            datamap.put("questions", questions);
            map.put("data", datamap);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }
	
	@PostMapping("/evaluate-interview")
	public ResponseEntity<?> function(@RequestParam("interview-id") String iid) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
	    LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
	    map.put("status", "OK");
	    HashMap<String, String> ans = DataCache.ans;
	    int scores[] = new int[8];
	    int cnt[] = new int[8];
	    	// 1. Software Development and Frameworks
	    	ans.put("What are microservices, and how do they differ from monolithic architecture?", 
	    	    "Microservices are a software development approach where applications are built as a collection of small, independent services that communicate via APIs. Unlike monolithic architectures, microservices allow scalability, independent deployment, and better fault isolation.");
	    	ans.put("What is dependency injection, and why is it used?", 
	    	    "Dependency Injection (DI) is a design pattern that allows objects to receive their dependencies from an external source rather than creating them internally. It improves modularity, testability, and makes code easier to maintain.");
	    	ans.put("Explain the difference between synchronous and asynchronous programming.", 
	    	    "Synchronous programming executes tasks sequentially, blocking execution until a task is completed. Asynchronous programming allows tasks to run concurrently, using callbacks, promises, or async/await to improve performance.");
//	    	ans.put("What are the key differences between REST and GraphQL?", 
//	    	    "REST is a standard architectural style using predefined endpoints and HTTP methods, whereas GraphQL is a query language that allows clients to specify the exact data they need, reducing over-fetching and under-fetching issues.");

	    	// 2. Databases and Optimization
	    	ans.put("What is database normalization, and why is it important?", 
	    	    "Normalization is the process of organizing database tables to minimize redundancy and improve data integrity. It helps reduce data anomalies and ensures efficient storage.");
	    	ans.put("Explain the differences between SQL and NoSQL databases.", 
	    	    "SQL databases are relational, use structured tables, and support ACID transactions. NoSQL databases are non-relational, store data in various formats (key-value, document, columnar, graph), and provide flexible scaling.");
	    	ans.put("What is an index in a database, and how does it improve performance?", 
	    	    "An index is a data structure that enhances database query speed by allowing faster lookups. However, excessive indexing can slow down insert, update, and delete operations.");
//	    	ans.put("What is sharding in databases, and when should it be used?", 
//	    	    "Sharding is a technique that distributes data across multiple databases or servers to improve scalability and performance. It is useful for handling large datasets and high-traffic applications.");

	    	// 3. DevOps and Deployment
	    	ans.put("What is CI/CD, and why is it important?", 
	    	    "Continuous Integration (CI) is the practice of automatically testing and merging code changes, while Continuous Deployment (CD) automates the release process. Together, they improve software quality and deployment speed.");
	    	ans.put("What is the difference between Docker and Kubernetes?", 
	    	    "Docker is a containerization platform that packages applications and their dependencies. Kubernetes is a container orchestration tool that manages container deployment, scaling, and networking.");
	    	ans.put("Explain Infrastructure as Code (IaC) and its benefits.", 
	    	    "IaC is the practice of managing infrastructure using code instead of manual configurations. It improves consistency, automation, and version control.");
//	    	ans.put("What is the purpose of a reverse proxy in deployment?", 
//	    	    "A reverse proxy sits in front of web servers and directs client requests to the appropriate backend servers, improving load balancing, security, and caching.");

	    	// 4. Problem Solving
	    	ans.put("How would you detect a cycle in a linked list?", 
	    	    "One approach is Floyd’s Cycle Detection Algorithm (Tortoise and Hare), where two pointers move at different speeds; if they meet, a cycle exists.");
	    	ans.put("What is dynamic programming, and when should you use it?", 
	    	    "Dynamic programming is an optimization technique that breaks problems into overlapping subproblems and stores solutions to avoid redundant computations. It is useful for problems like Fibonacci sequence, knapsack, and shortest path.");
//	    	ans.put("Explain the time complexity of quicksort.", 
//	    	    "Quicksort has an average and best-case time complexity of O(n log n) and a worst-case complexity of O(n²) when the pivot selection is poor.");

	    	// 5. Project Design
	    	ans.put("How do you design a URL shortener like Bitly?", 
	    	    "A URL shortener requires a mapping between long URLs and short keys. It can use a hashing mechanism, a database for storage, and caching for quick lookups.");
	    	ans.put("What are the key considerations when designing a scalable system?", 
	    	    "Factors include load balancing, database scaling, caching, distributed architecture, asynchronous processing, and failover strategies.");
//	    	ans.put("What is event-driven architecture, and where is it used?", 
//	    	    "Event-driven architecture processes events asynchronously using message queues, making it ideal for real-time applications like payment processing and IoT systems.");

	    	// 6. Troubleshooting
	    	ans.put("How do you debug a memory leak in a Java application?", 
	    	    "Use profiling tools like VisualVM or JProfiler to analyze heap usage. Look for objects that are not being garbage collected and check for improper resource management.");
	    	ans.put("What steps would you take to troubleshoot a slow database query?", 
	    	    "Analyze execution plans, check indexing, optimize queries, partition tables, and consider caching strategies.");

	    	// 7. Teamwork
//	    	ans.put("How do you handle disagreements in a team environment?", 
//	    	    "I focus on active listening, understanding different perspectives, and finding common ground. If needed, I escalate the issue to a manager for resolution.");
//	    	ans.put("Describe a time when you had to collaborate with a difficult team member.", 
//	    	    "I remained patient, communicated clearly, and focused on shared goals. I also sought feedback and adjusted my approach to improve teamwork.");
	    ArrayList<String> topic_who = new ArrayList<>();
	    topic_who.add("Software Development and Frameworks");
	    topic_who.add("Databases And Optimization");
	    topic_who.add("Devops And Deployment");
	    topic_who.add("Problem Solvings");
	    topic_who.add("Project Design");
	    topic_who.add("Troubleshooting");
	    String fans = "";
	    ArrayList<LinkedHashMap<String, Object>> arr = new ArrayList<>();
	    String ffans = "";
	    for(Map.Entry<String, String> entry : ans.entrySet()) {
	    	try {
	    		String ques = entry.getKey();
		    	String anss = entry.getValue();
		    	fans += anss + "\n";
		    	String prompt = "Evaluate the following interview question and answer. " +
		    	        "Question: \"" + ques + "\" " +
		    	        "Answer: \"" + anss + "\". " +
		    	        "Please assign a score out of 10 for the answer, determine which topic from the following list, and also give a short improvement" +
		    	        "(1. software development and frameworks, 2. databases and optimization, " +
		    	        "3. devops and deployment, 4. problem solving, 5. project design, " +
		    	        "6. troubleshooting) best describes this Q&A. Give the answer in format Score : ?, Topic : ?, Improvement : ?";
		    	   
	    	    String payload = buildPayload(prompt);
	    	    String response = sendApiRequest(payload);
	    	    System.out.println(response);
	    	    int id1 = response.indexOf("Score") + 7;
	    	    int id2 = response.indexOf(",", id1);
	    	    int score = Integer.parseInt(response.substring(id1, id2).trim());
	    	    id1 = response.indexOf("Topic") + 7;
	    	    id2 = response.indexOf(".", id1);
	    	    int topic = Integer.parseInt(response.substring(id1, id2).trim());
	    	    scores[topic] += score;
	    	    cnt[topic] ++;
	    	    id1 = response.indexOf("Improvement") + 14;
	    	    id2 = response.indexOf("}", id1);
	    	    String improve = response.substring(id1, id2).trim();
	    	    LinkedHashMap<String, Object> current = new LinkedHashMap<>();
	    	    current.put("question", ques);
	    	    current.put("topic", topic_who.get(topic - 1));
	    	    current.put("score", score);
	    	    current.put("improvement", improve);
	    	    ffans += ques + "$" + topic + "$" + score + "$" + improve + "$";
	    	    arr.add(current);
	    	}
	    	catch(Exception e) {}
	    }
	    String prompt = "Based on " + fans + "; List out Users 3 Strengths and 3 Weaknesses (Max 3 words per Strength and Weakness and mention real topics or subjects, not phrases)";
	    String payload = buildPayload(prompt);
	    String response = sendApiRequest(payload);
	    System.out.println("bhaa");
	    System.out.println(response);
	    List<String> go = karo(response);
	    String str = "", wk = "";
	    List<String> f = new ArrayList<>();
	    f.add(go.get(0));
	    f.add(go.get(1));
	    f.add(go.get(2));
	    str = f.get(0) + "$" + f.get(1) + "$" + f.get(2) + "$";
	    List<String> s = new ArrayList<>();
	    s.add(go.get(3));
	    s.add(go.get(4));
	    s.add(go.get(5));
	    wk = s.get(0) + "$" + s.get(1) + "$" + s.get(2) + "$"; 
	    for(int t=1;t<=7;t++) if(cnt[t] > 0) scores[t] /= cnt[t];
	    String scr = "";
	    LinkedHashMap<String, Integer> dd = new LinkedHashMap<>();
	    if(cnt[1] > 0) dd.put("Software Development and Frameworks", scores[1]);
	    if(cnt[2] > 0) dd.put("Databases And Optimization", scores[2]);
	    if(cnt[3] > 0) dd.put("Devops And Deployment", scores[3]);
	    if(cnt[4] > 0) dd.put("Problem Solvings", scores[4]);
	    if(cnt[5] > 0) dd.put("Project Design", scores[5]);
	    if(cnt[6] > 0) dd.put("Troubleshooting", scores[6]);
	    for(int j=1;j<=6;j++) scr += scores[j] + "$" + cnt[j] + "$";
	    Interview ii = i.getById(Integer.parseInt(iid));
	    ii.setAnalysis(ffans);
	    ii.setScore(scr);
	    ii.setStrengths(str);
	    ii.setWeaknesses(wk);
	    i.save(ii);
	    datamap.put("scores", dd);
	    datamap.put("strengths", f);
	    datamap.put("weaknesses", s);
	    datamap.put("analysis", arr);
	    map.put("data", datamap);
	    return ResponseEntity.ok(map);
	}
	
	@PostMapping("/follow-up-question")
	public ResponseEntity<?> pp1(
	        @RequestParam("followup-id") String fid,
	        @RequestParam("question-id") String qid,
	        @RequestParam("answer") String ans) {
		int p1 = Integer.parseInt(fid);
		int p2 = Integer.parseInt(qid);
		ArrayList<Integer> ques = new ArrayList<>();
		ques.add(p1);
		ques.add(p2);
		String which_question = DataCache.ques.get(ques);
		DataCache.ans.put(which_question, ans);
		String prompt = "Based on the following answer, generate a follow-up question:\nAnswer: " + ans;
	    String payload = buildPayload(prompt);
	    String aiResponse = sendApiRequest(payload);
	    System.out.println(aiResponse);
	    String followUpQuestion = aiResponse.substring(aiResponse.indexOf("\"content\"") + 11, aiResponse.indexOf("\"logprobs\"") - 3);

	    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
	    LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
	    
	    map.put("status", "OK");
	    LinkedHashMap<String, Object> question = new LinkedHashMap<>();
	    question.put("question-id", qid);
	    question.put("followup-id", Math.max(1, Integer.parseInt(fid) + 1));
	    question.put("description", followUpQuestion);
	    int n1 = Integer.parseInt(qid);
	    int n2 = Math.max(1, Integer.parseInt(fid) + 1);
	    ArrayList<Integer> nques = new ArrayList<>();
	    nques.add(n1);
	    nques.add(n2);
	    DataCache.ques.put(nques, followUpQuestion);
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
    
    private static List<String> karo(String apiResponse) {
        List<String> questions = new ArrayList<>();
        String str = "";
        String find = "1.";
        for(int i=0;i<apiResponse.length();i++) {
        	if(i + find.length() > apiResponse.length()) break;
        	if(apiResponse.substring(i, i + find.length()).equals(find)) {
        		int idx = i + find.length() + 1;
        		str = "";
        		while(true) {
        			if(apiResponse.charAt(idx) >= 48 && apiResponse.charAt(idx) <= 57) break;
        			if(apiResponse.charAt(idx) == '\"') break;
        			if(idx == apiResponse.indexOf("Weaknesses:")) break;
        			str += apiResponse.charAt(idx);
        			idx++;
        		}
        		System.out.println(str);
        		questions.add(str);
        		i = idx - 1;
        		if(find.equals("1.")) find = "2.";
        		else if(find.equals("2.")) find = "3.";
        		else if(find.equals("3.")) find = "1.";
        	}
        }
        System.out.println(questions);
        return questions;
    }
    
    private static List<LinkedHashMap<String, Object>> parseQuestions(String apiResponse, int count) {
        List<LinkedHashMap<String, Object>> questions = new ArrayList<>();
        int ind = -1;
        String str = "";
        String find = "1.";
        int ctr = 1;
        int itr = 1;
        for(int i=0;i<apiResponse.length();i++) {
        	if(ctr > count) break;
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
        		ArrayList<Integer> ppp = new ArrayList<>();
        		ppp.add(itr);
        		ppp.add(0);
        		DataCache.ques.put(ppp, str);
        		questions.add(pp);
        		i = idx;
        		ctr++;
        		find = ctr + ".";
        	}
        }
        System.out.println(questions);
        return questions;
    }
    
    private static List<LinkedHashMap<String, Object>> parseQuestions(String apiResponse) {
        List<LinkedHashMap<String, Object>> questions = new ArrayList<>();
        int ind = -1;
        String str = "";
        String find = "1.";
        int itr = 1;
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
        		ArrayList<Integer> ppp = new ArrayList<>();
        		ppp.add(itr);
        		ppp.add(0);
        		DataCache.ques.put(ppp, str);
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
        		else break;
        	}
        }
        System.out.println(questions);
        return questions;
    }
}
