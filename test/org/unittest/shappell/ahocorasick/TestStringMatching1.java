package org.unittest.shappell.ahocorasick;

import java.util.List;

import org.shappell.ahocorasick.EntityDictionary;
import org.shappell.ahocorasick.StringSearchResult;
import org.shappell.ahocorasick.entity.StringEntity;
import org.testng.annotations.Test;


public class TestStringMatching1 {
	
	@Test
	public void BasicStringMatching1() {
		String[] vals = {"one","two","three","four","three or four"};
		String text = "one three or four or four two";
		
		EntityDictionary dict = CreateDictionaryWithStrings(vals, true);
		
		List<StringSearchResult> res = dict.FindAll(text);
		
		displayResults(res);
	}
	
	public void displayResults(List<StringSearchResult> res) {
		if(res == null){
			System.out.println("results = NULL");
		return;
		}
		
		for(StringSearchResult r : res) {
			System.out.println("res = " + r);
		}
	}
	
	public EntityDictionary CreateDictionaryWithStrings(String[] strings, boolean caseInsensitive) {
		
		EntityDictionary dict = new EntityDictionary();
		dict.setLowercase(caseInsensitive);
		
		for(int i =0 ; i < strings.length; ++i)
			dict.AddEntity(new StringEntity(strings[i]));
		
		dict.optimize();
		
		return dict;
	}
}
