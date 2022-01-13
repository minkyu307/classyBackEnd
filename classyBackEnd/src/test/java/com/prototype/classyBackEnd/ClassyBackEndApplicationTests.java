package com.prototype.classyBackEnd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.net.URL;

@SpringBootTest
class ClassyBackEndApplicationTests {

	@Autowired
	ResourceLoader resourceLoader;


	@Test
	void contextLoads() {
		URL r = this.getClass().getResource("");
		String path = r.getPath();
		System.out.println("path = " + path);
	}

	@Test
	void staticPath(){
		Resource resource = resourceLoader.getResource("classpath:static/img.jpg");
		String path = resource.getFilename();
		System.out.println("path = " + path);
	}
}
