package com.esave.common;

import java.util.HashMap;

import com.esave.entities.PurveyorDetails;

public class PurveyorRegister {
	
	private static HashMap<String,PurveyorDetails> register;
	
	private PurveyorRegister(){
		
	}
	
	public static HashMap<String,PurveyorDetails> getInstance () {
        if (register == null) {
        	synchronized (PurveyorRegister.class) {
        		if (register == null) {
        			register = new HashMap<String, PurveyorDetails> ();
        		}
        	}
        }
        return register;
    }
	
}
