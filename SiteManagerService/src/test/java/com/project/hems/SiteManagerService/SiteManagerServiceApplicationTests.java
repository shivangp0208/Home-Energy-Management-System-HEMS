package com.project.hems.SiteManagerService;

import com.netflix.discovery.converters.Auto;
import com.project.hems.SiteManagerService.service.SiteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SiteManagerServiceApplicationTests {

	@Autowired
	private SiteService siteService;
	@Test
	void contextLoads() {
		this.siteService.fetchAllRegion();
	}

}
