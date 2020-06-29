package spring.cloud.demo.websocket.feign;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "hello-world",url = "http://localhost:8090/")
public interface FeignDemo {

	@GetMapping("/index")
	public String queryIndex();


	@GetMapping("/hystrix")
	@HystrixCommand(fallbackMethod = "handleError")
	public String hystrix();

}
