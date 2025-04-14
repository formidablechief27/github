package com.example.prep.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.prep.model.Mcq;

public class DataCache {
	public static HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
	public static HashMap<ArrayList<Integer>, String> ques = new HashMap<>();
	public static HashMap<String, String> ans = new HashMap<>();
	public static List<String> topics = new ArrayList<>();
	public static List<Mcq> aptitude = new ArrayList<>();
	public static List<Mcq> technical = new ArrayList<>();
	public static List<Mcq> english = new ArrayList<>();
	public static List<Mcq> core = new ArrayList<>();
}
