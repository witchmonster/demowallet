package com.jkramr.demowalletclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"demowallet.client.grpc_channel=mock"})
public class DemowalletClientApplicationTests {

	@Test
	public void contextLoads() {
	}

}
