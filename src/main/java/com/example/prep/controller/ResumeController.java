package com.example.prep.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
            	strengths.add(fix(s1));
            	strengths.add(fix(s2));
            	strengths.add(fix(s3));
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
            	weaknesses.add(fix(s4));
            	weaknesses.add(fix(s5));
            	weaknesses.add(fix(s6));
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
            Collections.shuffle(questions);;
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
			DataCache.topics.clear();
			for(String ele : topics) DataCache.topics.add(ele);
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
            Collections.shuffle(questions);
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
            DataCache.ques.clear();
            List<LinkedHashMap<String, Object>> questions = parseQuestions(apiResponse);
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
            System.out.println(DataCache.ques);
            map.put("status", "OK");
            datamap.put("interview-id", id);
            Collections.shuffle(questions);
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
	    ArrayList<String> topic_who = new ArrayList<>();
	    topic_who.add("Software Development and Frameworks");
	    topic_who.add("Databases And Optimization");
	    topic_who.add("Devops And Deployment");
	    topic_who.add("Problem Solvings");
	    topic_who.add("Project Design");
	    DataCache.ans.clear();
	    DataCache.ans.put("can you describe your experience with identifying and resolving bugs within projects and how you ensure seamless functionality and optimal performance.", "To ensure a seamless process, establish clear workflows, automate repetitive tasks, maintain thorough documentation, and communicate effectively across teams.");
	    DataCache.ans.put("What specific tools or strategies would you recommend for automating repetitive tasks and maintaining thorough documentation to support these clear workflows and effective team communication?", "I would suggest tools like Jenkins, GitHub Actions, Zapier, Ansible, and Selenium depending on whether you're automating builds, deployments, workflows, or testing.");
	    DataCache.ans.put("What are some key considerations or factors that I should take into account when choosing between these automation tools such as Jenkins, Github actions, Zapier and Selenium for a specific project or task?s", "CI/CD → Jenkins, GitHub Actions"
	    		+ ";Workflow automation → Zapier"
	    		+ ";Server configuration → Ansible"
	    		+ ";Testing automation → Selenium");
	    DataCache.ans.put("How do you approach enhancing user interface design to create an aesthetically pleasing and visually appealing user experience?", "Focus on simplicity, consistency, intuitive navigation, and visually balanced layouts with a user-first mindset.");
	    DataCache.ans.put("How can a designer effectively balance the need for simplicity and consistency in a user interface with the desire to incorporate unique and creative visual elements that enhance the user experience?", "Prioritize core functionality first, then introduce innovative elements that enhance — not complicate — the user experience.");
	    DataCache.ans.put("What inspired you to develop user friendly website using HTML, CSS, JavaScript and Bootstrap and what were some challenges you faced?", "Inspiration comes from creativity and real-world needs, while challenges include responsive design, cross-browser compatibility, and maintaining clean, structured code.");
	    DataCache.ans.put("How do developers typically balance the need for creative and innovative design with the technical challenge of ensuring cross border compatibility and maintaining clean, structured code?", "Use standardized HTML/CSS, test across major browsers regularly, and apply fallbacks or polyfills for unsupported features.");
	    DataCache.ans.put("How do you determine which specific fallbacks or polyfills to implement for unsupported features in different browsers and are there any tools or resources that you rely on to help with these processes?", "Use CanIUse or Modernizr to detect unsupported features and pick fallbacks or polyfills based on target browser support.");
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
		    	        "3. devops and deployment, 4. problem solving, 5. project design)" +
		    	        " best describes this Q&A. Give the answer in format Score : ?, Topic : ?, Improvement : ?";
		    	   
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
	    response = response.replaceAll("\n", "");
	    System.out.println("bhaa");
	    System.out.println(response);
	    List<String> go = karo(response);
	    String str = "", wk = "";
	    List<String> f = new ArrayList<>();
	    f.add(go.get(0).substring(0, go.get(0).length() - 2));
	    f.add(go.get(1).substring(0, go.get(1).length() - 2));
	    f.add(go.get(2).substring(0, go.get(2).length() - 4));
	    System.out.println(f.get(0).length());
	    str = f.get(0) + "$" + f.get(1) + "$" + f.get(2) + "$";
	    List<String> s = new ArrayList<>();
	    s.add(go.get(3).substring(0, go.get(3).length() - 2));
	    s.add(go.get(4).substring(0, go.get(4).length() - 2));
	    s.add(fix(go.get(5)));
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
	    DataCache.ans.clear();
	    return ResponseEntity.ok(map);
	}
	
	@PostMapping("/evaluate-custom-interview")
	public ResponseEntity<?> gogo(@RequestParam("interview-id") String iid) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
	    LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
	    map.put("status", "OK");
	    HashMap<String, String> ans = DataCache.ans;
	    int scores[] = new int[8];
	    int cnt[] = new int[8];
	    ArrayList<String> topic_who = new ArrayList<>();
	    String frame = "";
	    for(String ele : DataCache.topics) topic_who.add(ele);
	    int ctr = 1;
	    frame = "(";
	    for(String ele : topic_who) {
	    	String pp = ctr + ". ";
	    	pp += ele + ", ";
	    	frame += pp;
	    	ctr++;
	    }
	    frame += ")";
	    String fans = "";
	    ArrayList<LinkedHashMap<String, Object>> arr = new ArrayList<>();
	    String ffans = "";
	    for(Map.Entry<String, String> entry : ans.entrySet()) {
	    	try {
	    		String ques = entry.getKey();
		    	String anss = entry.getValue();
		    	System.out.println(ques + "%%" + anss);
		    	fans += anss + "\n";
		    	String prompt = "Evaluate the following interview question and answer. " +
		    	        "Question: \"" + ques + "\" " +
		    	        "Answer: \"" + anss + "\". " +
		    	        "Please assign a score out of 10 for the answer, determine which (exactly 1) topic from the following list, and also give a short improvement " +
		    	        frame + " best describes this Q&A. Give the answer in format Score : ?, Topic : ?, Improvement : ?";
		    	   
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
	    f.add(fix(go.get(0)));
	    f.add(fix(go.get(1)));
	    f.add(fix(go.get(2)));
	    str = f.get(0) + "$" + f.get(1) + "$" + f.get(2) + "$";
	    List<String> s = new ArrayList<>();
	    s.add(fix(go.get(3)));
	    s.add(fix(go.get(4)));
	    s.add(fix(go.get(5)));
	    wk = s.get(0) + "$" + s.get(1) + "$" + s.get(2) + "$"; 
	    for(int t=1;t<=7;t++) if(cnt[t] > 0) scores[t] /= cnt[t];
	    String scr = "";
	    LinkedHashMap<String, Integer> dd = new LinkedHashMap<>();
	    for(int i=0;i<6;i++) {
	    	if(cnt[i + 1] > 0) dd.put(topic_who.get(i), scores[i + 1]);
	    }
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
	    DataCache.ans.clear();
	    return ResponseEntity.ok(map);
	}
	
	@PostMapping("/evaluate-hr-interview")
	public ResponseEntity<?> gogogo(@RequestParam("interview-id") String iid) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
	    LinkedHashMap<String, Object> datamap = new LinkedHashMap<>();
	    map.put("status", "OK");
	    HashMap<String, String> ans = DataCache.ans;
	    int scores[] = new int[8];
	    int cnt[] = new int[8];
	    ArrayList<String> topic_who = new ArrayList<>();
	    String frame = "";
	    topic_who.add("Company Knowledge");
	    topic_who.add("Behavoiural Knowledge");
	    topic_who.add("Situational Knowledge");
	    int ctr = 1;
	    frame = "(";
	    for(String ele : topic_who) {
	    	String pp = ctr + ". ";
	    	pp += ele + ", ";
	    	frame += pp;
	    	ctr++;
	    }
	    frame += ")";
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
		    	        "Please assign a score out of 10 for the answer, determine which (exactly 1) topic from the following list, and also give a short improvement " +
		    	        frame + " best describes this Q&A. Give the answer in format Score : ?, Topic : ?, Improvement : ?";
		    	   
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
	    String prompt = "Based on " + fans + "; List out Users 3 Strengths and 3 Weaknesses (Max 3 words per Strength and Weakness and mention real topics or subjects, not phrases; please answer on the basis of hr interview, not a technical interview)";
	    String payload = buildPayload(prompt);
	    String response = sendApiRequest(payload);
	    System.out.println("bhaa");
	    System.out.println(response);
	    List<String> go = karo(response);
	    String str = "", wk = "";
	    List<String> f = new ArrayList<>();
	    f.add(fix(go.get(0)));
	    f.add(fix(go.get(1)));
	    f.add(fix(go.get(2)));
	    str = f.get(0) + "$" + f.get(1) + "$" + f.get(2) + "$";
	    List<String> s = new ArrayList<>();
	    s.add(fix(go.get(3)));
	    s.add(fix(go.get(4)));
	    s.add(fix(go.get(5)));
	    wk = s.get(0) + "$" + s.get(1) + "$" + s.get(2) + "$"; 
	    for(int t=1;t<=7;t++) if(cnt[t] > 0) scores[t] /= cnt[t];
	    String scr = "";
	    LinkedHashMap<String, Integer> dd = new LinkedHashMap<>();
	    for(int i=0;i<6;i++) {
	    	if(cnt[i + 1] > 0) dd.put(topic_who.get(i), scores[i + 1]);
	    }
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
	    DataCache.ans.clear();
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
		ques.add(p2);
		ques.add(p1);
		String which_question = DataCache.ques.get(ques);
		DataCache.ans.put(which_question, ans);
		System.out.println(DataCache.ans);
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
	    System.out.println(DataCache.ques);
	    datamap.put("question", question);
	    map.put("data", datamap);

	    return ResponseEntity.ok(map);
	}
	
	public String fix(String data) {
		data = data.replaceAll("\\n+$", "");
		return data;
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
        			if(apiResponse.charAt(idx) == '\n' || apiResponse.charAt(idx) == '\r') {
        				idx++;
        		        break;
        		    }
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
        		ppp.add(ctr);
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
        		itr++;
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
