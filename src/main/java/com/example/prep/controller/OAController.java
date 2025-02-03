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

import com.example.prep.model.Company;
import com.example.prep.model.Mcq;
import com.example.prep.model.Questions;
import com.example.prep.model.Testcases;
import com.example.prep.repository.CompanyRepository;
import com.example.prep.repository.DataCache;
import com.example.prep.repository.McqRepository;
import com.example.prep.repository.QuestionRepository;
import com.example.prep.repository.TestcasesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class OAController {
	
	@Autowired
	QuestionRepository repo;
	TestcasesRepository t_repo;
	CompanyRepository c_repo;
	McqRepository m_repo;
	
	@Autowired
	OAController(QuestionRepository repo, TestcasesRepository t_repo, CompanyRepository c_repo, McqRepository m_repo) {
		this.repo = repo;
		this.t_repo = t_repo;
		this.c_repo = c_repo;
		this.m_repo = m_repo;
	}
	
	@PostMapping("/sections")
	public ResponseEntity<String> section(@RequestBody Map<String, Integer> requestBody) {
	    Integer id = requestBody.get("company-id");
	    
	    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ArrayList<HashMap<String, String>> map = new ArrayList<>();
	    Optional<Company> opt = c_repo.findById(id);
	    List<Mcq> list = m_repo.findAll();
	    if(opt.isPresent()) {
	    	int idd = 0;
	    	for(int i=0;i<opt.get().getDsa();i++) {
	    		HashMap<String, String> fmap = new HashMap<>();
	    		idd++;
	    		String p = idd + "";
	    		fmap.put("ques-id", p);
	    		fmap.put("name", "Coding Problem");
	    		int mks = 20 + (10 * i);
	    		ArrayList<Integer> mm = new ArrayList<>();
	    		mm.add(repo.findById(idd).get().getId());
	    		DataCache.map.put(idd, mm);
	    		String m = mks + "";
	    		fmap.put("marks", m);
	    		map.add(fmap);
	    	}
	    	if(opt.get().getAptitude() > 0) {
	    		idd++;
	    		String p = idd + "";
	    		HashMap<String, String> fmap = new HashMap<>();
	    		fmap.put("section-id", p);
	    		fmap.put("name", "Aptitude");
	    		int mks = opt.get().getAptitude();
	    		ArrayList<Integer> mm = new ArrayList<>();
	    		int cnt = mks;
	    		for(Mcq mcq : list) {
	    			if(mcq.getType().trim().equals("apt")) {
	    				if(cnt > 0) {
	    					cnt--;
	    					long ip = mcq.getId();
	    					mm.add((int)ip);
	    				}
	    			}
	    		}
	    		DataCache.map.put(idd, mm);
	    		String m = mks + "";
	    		fmap.put("marks", m);
	    		map.add(fmap);
	    	}
	    	if(opt.get().getCore() > 0) {
	    		idd++;
	    		String p = idd + "";
	    		HashMap<String, String> fmap = new HashMap<>();
	    		fmap.put("section-id", p);
	    		fmap.put("name", "Core");
	    		int mks = opt.get().getCore();
	    		String m = mks + "";
	    		ArrayList<Integer> mm = new ArrayList<>();
	    		int cnt = mks;
	    		for(Mcq mcq : list) {
	    			if(mcq.getType().trim().equals("core")) {
	    				if(cnt > 0) {
	    					cnt--;
	    					long ip = mcq.getId();
	    					mm.add((int)ip);
	    				}
	    			}
	    		}
	    		DataCache.map.put(idd, mm);
	    		fmap.put("marks", m);
	    		map.add(fmap);
	    	}
	    	if(opt.get().getEnglish() > 0) {
	    		idd++;
	    		String p = idd + "";
	    		HashMap<String, String> fmap = new HashMap<>();
	    		fmap.put("section-id", p);
	    		fmap.put("name", "English");
	    		int mks = opt.get().getEnglish();
	    		String m = mks + "";
	    		ArrayList<Integer> mm = new ArrayList<>();
	    		int cnt = mks;
	    		for(Mcq mcq : list) {
	    			System.out.println(mcq.getType());
	    			if(mcq.getType().trim().equals("eng")) {
	    				if(cnt > 0) {
	    					cnt--;
	    					long ip = mcq.getId();
	    					mm.add((int)ip);
	    				}
	    			}
	    		}
	    		DataCache.map.put(idd, mm);
	    		fmap.put("marks", m);
	    		map.add(fmap);
	    	}
	    }
	    responseMap.put("status", "OK");
	    responseMap.put("data", map);
	    System.out.println(DataCache.map);
	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }

	@PostMapping({"/mcq-details", "/technical", "/aptitude", "/english"})
	public ResponseEntity<String> mcqrepository(@RequestBody Map<String, Integer> requestBody) {
	    Integer id = requestBody.get("section-id");
	    Integer id2 = requestBody.get("company-id");
	    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    List<Mcq> mcq = m_repo.findAll();
	    HashMap<Long, Mcq> map = new HashMap<>();
	    for(Mcq m : mcq) map.put(m.getId(), m);
	    ArrayList<Integer> arr = DataCache.map.get(id);
	    System.out.println(arr);
	    List<Mcq> flist = new ArrayList<>();
	    for(int ele : arr) flist.add(map.get((long)ele));

	    responseMap.put("status", "OK");
	    responseMap.put("data", flist);

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
