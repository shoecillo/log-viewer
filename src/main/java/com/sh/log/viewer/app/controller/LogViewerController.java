package com.sh.log.viewer.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sh.log.viewer.app.exception.AppNotFoundException;
import com.sh.log.viewer.app.model.AppLogsDTO;

@RestController
public class LogViewerController {
	
	@Value("${log.source.path}")
	private String rootPath;
	
	@Autowired
	private List<AppLogsDTO> appPaths;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LogViewerController.class);
	
	
	@GetMapping("/getFile/{app}/{filename}")
	public ResponseEntity<Resource> getFile(@PathVariable(name="app") String app, @PathVariable(name="filename") String filename) throws Exception{
		
		Path root = Paths.get(filterApp(app).getPath());
		Path file = root.resolve(filename);
		Resource res = new ByteArrayResource(Files.readAllBytes(file));
		
		 HttpHeaders header = new HttpHeaders();
	     header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=".concat(file.getFileName().toString()));
		
		return ResponseEntity.ok()
				.headers(header)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.contentLength(file.toFile().length())
				.body(res);
	}
	
	@PostMapping(path="/getFileContent/{app}/{filename}",produces="text/html")
	public ResponseEntity<String> getFileContent(@PathVariable(name="app") String app, @PathVariable(name="filename") String filename) throws Exception{
		
		Path root = Paths.get(filterApp(app).getPath());
		Path file = root.resolve(filename);
		String text = Files.readAllLines(file).stream().collect(Collectors.joining("<br>"));
		return new ResponseEntity<String>(text, HttpStatus.OK);
	}
	
	@GetMapping("/getLogs/{app}")
	public ResponseEntity<ArrayNode> getLogs(@PathVariable("app") String app) throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode json = mapper.createArrayNode();
		
		Path root = Paths.get(filterApp(app).getPath());
		
		Files.list(root).forEach(it->{
			try {
				ObjectNode node = mapper.createObjectNode()
						.put("path", it.getFileName().toString())
						.put("size", convertSize(Files.size(it)));
				
				json.add(node);
			} catch (IOException e) {
				
				LOGGER.error("Error: ",e);
			}
		});
		
		return new ResponseEntity<ArrayNode>(json,HttpStatus.OK);
		
		
	}
	
	@GetMapping("/getAppz")
	public ResponseEntity<ArrayNode> getAppz(){
		
		ObjectMapper mapper = new ObjectMapper();
		
		List<JsonNode> ls = appPaths.stream()
				.map(it->mapper.createObjectNode().put("app", it.getApplication()))
				.collect(Collectors.toList());
		
		ArrayNode res = mapper.createArrayNode().addAll(ls);
		
		return new ResponseEntity<ArrayNode>(res, HttpStatus.OK);
	}
	
	private String convertSize(Long sizeBytes) {
		
		String text = "";
		Float res = (float) (sizeBytes/1024F);
		DecimalFormat fmt = new DecimalFormat("#.##");
		if(res>1000F) {
			res = (float) (res/1024F);
			text = fmt.format(res).concat(" Mb");
		} else {
			text = fmt.format(res).concat(" Kb");
		}
		return text;
		
	}
	
	private AppLogsDTO filterApp(String app) throws AppNotFoundException {
		List<AppLogsDTO> ls = appPaths.stream()
				.filter(it->it.getApplication().equals(app))
				.collect(Collectors.toList());
		
		if(ls.isEmpty()) {
			throw new AppNotFoundException("App Not Found");
		}
		
		return ls.get(0);
	}
	
}
