package com.example.prep.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.prep.model.Company;
import com.example.prep.model.Questions;
import com.example.prep.model.Testcases;
import com.example.prep.repository.CompanyRepository;
import com.example.prep.repository.QuestionRepository;
import com.example.prep.repository.TestcasesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class QuestionController {
	
	@Autowired
	QuestionRepository repo;
	TestcasesRepository t_repo;
	CompanyRepository c_repo;
	
	@Autowired
	QuestionController(QuestionRepository repo, TestcasesRepository t_repo, CompanyRepository c_repo) {
		this.repo = repo;
		this.t_repo = t_repo;
		this.c_repo = c_repo;
	}
	
	@PostMapping("/questions")
	public ResponseEntity<String> questionSearch(@RequestBody Map<String, String> requestBody) {
	    String tag = requestBody.get("tag");
	    System.out.println("Tag Selected => " + tag);

	    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<Questions> list = repo.findAll();
	    List<Questions> data = new ArrayList<>();

	    for (Questions q : list) {
	        if (q.getTag().equalsIgnoreCase(tag)) {
	            data.add(q);
	        }
	    }
	    responseMap.put("status", "OK");
	    responseMap.put("data", data);

	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }
	}
	
	@PostMapping("/description")
	public ResponseEntity<String> questionDescription(@RequestBody Map<String, Integer> requestBody) {
	    Integer id = requestBody.get("id");
	    System.out.println("Id Selected => " + id);
	    
	    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    Optional<Questions> opt = repo.findById(id);
	    if(opt.isEmpty()){
	        responseMap.put("status", "FAILED");
	        responseMap.put("data", "");
	    }
	    List<Testcases> list = t_repo.findAll();
	    List<Testcases> flist = new ArrayList<>();
	    for(Testcases t : list) if(t.getId() >= opt.get().getTestcaseStart() && t.getId() <= opt.get().getTestcaseEnd()) flist.add(t);
	    LinkedHashMap<String, Object> data_map = new LinkedHashMap<>();
	    data_map.put("question", opt.get());
	    data_map.put("tests", flist);
	    if (opt.isPresent()) {
	        responseMap.put("status", "OK");
	        responseMap.put("data", data_map);
	        
	    } else {
	        responseMap.put("status", "FAILED");
	        responseMap.put("data", "");
	    }

	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }
	}
	
	@PostMapping("/company")
	public ResponseEntity<String> company(){
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<Company> list = c_repo.findAll();
	    responseMap.put("status", "OK");
	    responseMap.put("data", list);
	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }
	}

	
	@PostMapping("/topics")
	public ResponseEntity<String> topics_description(){
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    ObjectMapper objectMapper = new ObjectMapper();
	    ArrayList<String> list = new ArrayList<>();
	    list.add("Brute Force");
	    list.add("Priority Queues");
	    list.add("Maps and Sets");
	    list.add("Binary Search");
	    list.add("Trees");
	    list.add("Greedy");
	    list.add("Math");
	    list.add("Strings");
	    list.add("Dynamic Programming");
	    list.add("Graphs");
	    responseMap.put("status", "OK");
	    responseMap.put("data", list);
	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }
	}
	
}
