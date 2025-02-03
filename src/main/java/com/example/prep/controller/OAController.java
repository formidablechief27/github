package com.example.prep.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	
	Object dsa(int id) {
		Optional<Questions> opt = repo.findById(id);
		List<Testcases> list = t_repo.findAll();
	    List<Testcases> flist = new ArrayList<>();
	    for(Testcases t : list) if(t.getId() >= opt.get().getTestcaseStart() && t.getId() <= opt.get().getTestcaseEnd()) flist.add(t);
	    LinkedHashMap<String, Object> data_map = new LinkedHashMap<>();
	    data_map.put("question", opt.get());
	    data_map.put("tests", flist);
	    return data_map;
	}
	
	Object mcq(HashMap<Long, Mcq> map, ArrayList<Integer> arr) {
	    List<Mcq> flist = new ArrayList<>();
	    for(int ele : arr) flist.add(map.get((long)ele));
	    return flist;
	}
	
	@PostMapping("/question-data")
	public ResponseEntity<String> ques(@RequestBody Map<String, Integer> requestBody) {
	    Integer id = requestBody.get("company-id");
	    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    List<Mcq> mcq = m_repo.findAll();
	    HashMap<Long, Mcq> map = new HashMap<>();
	    for(Mcq m : mcq) map.put(m.getId(), m);
	    ObjectMapper objectMapper = new ObjectMapper();
	    Optional<Company> opt = c_repo.findById(id);
	    responseMap.put("status", "OK");
	    List<Mcq> list = m_repo.findAll();
	    if(opt.isPresent()) {
	    	int idd = 0;
	    	ArrayList<Object> ll = new ArrayList<>();
	    	for(int i=0;i<opt.get().getDsa();i++) {
	    		idd++;
	    		ll.add(dsa(repo.findById(idd).get().getId()));
	    	}
	    	responseMap.put("dsa", ll);
	    	if(opt.get().getAptitude() > 0) {
	    		idd++;
	    		int mks = opt.get().getAptitude();
	    		ArrayList<Integer> mm = new ArrayList<>();
	    		int cnt = mks;
	    		for(Mcq mcqq : list) {
	    			if(mcqq.getType().trim().equals("apt")) {
	    				if(cnt > 0) {
	    					cnt--;
	    					long ip = mcqq.getId();
	    					mm.add((int)ip);
	    				}
	    			}
	    		}
	    		responseMap.put("aptitude", mcq(map, mm));
	    	}
	    	if(opt.get().getCore() > 0) {
	    		idd++;
	    		int mks = opt.get().getCore();
	    		ArrayList<Integer> mm = new ArrayList<>();
	    		int cnt = mks;
	    		for(Mcq mcqq : list) {
	    			if(mcqq.getType().trim().equals("core")) {
	    				if(cnt > 0) {
	    					cnt--;
	    					long ip = mcqq.getId();
	    					mm.add((int)ip);
	    				}
	    			}
	    		}
	    		responseMap.put("technical", mcq(map, mm));
	    	}
	    	if(opt.get().getEnglish() > 0) {
	    		idd++;
	    		int mks = opt.get().getEnglish();
	    		ArrayList<Integer> mm = new ArrayList<>();
	    		int cnt = mks;
	    		for(Mcq mcqq : list) {
	    			if(mcqq.getType().trim().equals("eng")) {
	    				if(cnt > 0) {
	    					cnt--;
	    					long ip = mcqq.getId();
	    					mm.add((int)ip);
	    				}
	    			}
	    		}
	    		responseMap.put("english", mcq(map, mm));
	    	}
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
	
	@PostMapping("/sections")
	public ResponseEntity<String> section(@RequestBody Map<String, Integer> requestBody) {
	    Integer id = requestBody.get("company-id");
	    
	    LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ArrayList<HashMap<String, String>> map = new ArrayList<>();
	    Optional<Company> opt = c_repo.findById(id);
	    List<Mcq> list = m_repo.findAll();
	    HashSet<Integer> ignore = new HashSet<>();
	    if(opt.isPresent()) {
	    	int idd = 0;
	    	for(int i=0;i<opt.get().getDsa();i++) {
	    		HashMap<String, String> fmap = new HashMap<>();
	    		idd++;
	    		ignore.add(idd);
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
	}
}
