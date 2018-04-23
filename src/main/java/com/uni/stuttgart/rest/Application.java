package com.uni.stuttgart.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import org.springframework.web.bind.annotation.RestController;

import com.uni.stuttgart.database.*;


@SpringBootApplication
public class Application {

//Hier manuell den Pfad setzen, falls Pfad setzen über setPath.txt nicht funktioniert
private static String path;

public static void readPath(){
	
	//Hier wird der Pfad zum Graphen eingelesen
	File file = new File("src//main//java//com//uni//stuttgart//rest//setPath.txt");
	BufferedReader br = null;
	FileInputStream fr = null;
	
	try {

		String sCurrentLine;
		fr = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(fr));
		
		while((sCurrentLine = br.readLine()).toCharArray()[0] == '-'){
			
		}
		//sCurrentLine = br.readLine();
		if(sCurrentLine != null){
			while(sCurrentLine.toCharArray()[0] == '#'){
				sCurrentLine = br.readLine();
			}
			path = sCurrentLine;
		}
		
	} catch(IOException e){
		e.printStackTrace();
	}
}
	

	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run(Application.class, args);
		//Die Zeile hierunter auskommentieren, falls Pfad setzen über setPath.txt nicht funktioniert
		readPath();
		context.getBean(Controller.class).init(path);
	}

}
