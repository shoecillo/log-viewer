package com.sh.log.viewer.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.log.viewer.app.model.AppLogsDTO;

@SpringBootApplication
public class LogViewerApp {

	@Value("${source.json}")
	private String jsonSource;
	
	
	public static void main(String ...args) {
		SpringApplication.run(LogViewerApp.class, args);
	}
	
	@Bean
	public List<AppLogsDTO> appPaths() throws IOException{
				
		File f = new File(jsonSource);
		ObjectMapper mapper = new ObjectMapper();
		if(!f.exists()) {
			AppLogsDTO dto = new AppLogsDTO();
			List<AppLogsDTO> ls = new ArrayList<>();
			ls.add(dto);
			mapper.writeValue(f, ls);
			return new ArrayList<AppLogsDTO>();
			
		} else {
			return mapper.readValue(f,new TypeReference<List<AppLogsDTO>>(){});
		}
		
	}
	
}
