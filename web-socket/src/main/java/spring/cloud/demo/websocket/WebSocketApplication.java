package spring.cloud.demo.websocket;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import spring.cloud.demo.websocket.dao.UserDao;
import spring.cloud.demo.websocket.feign.FeignDemo;
import spring.cloud.demo.websocket.websocket.WebSocketServer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@SpringBootApplication
@MapperScan
@EnableFeignClients
@EnableCircuitBreaker
public class WebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }

    @Autowired
    private UserDao userDao;

    @GetMapping("/user")
    @Transactional(rollbackFor = Throwable.class)
    public int countUser(){
        int count = userDao.count();
        count+=userDao.count();
        return count;
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @GetMapping("index")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("请求成功");
    }

    @GetMapping("page")
    public ModelAndView page() {
        return new ModelAndView("websocket");
    }

    @RequestMapping("/push/{toUserId}")
    public ResponseEntity<String> pushToWeb(String message, @PathVariable String toUserId) throws IOException {
        WebSocketServer.sendInfo(message, toUserId);
        return ResponseEntity.ok("MSG SEND SUCCESS");
    }

    @GetMapping("/redirect")
    public String redirectTest(HttpServletResponse response, RedirectAttributes attr) throws IOException {
        attr.addAttribute("name", "hdr");
        return "redirect:/index";
    }

    @Autowired
    private FeignDemo feignDemo;

    @GetMapping("feign")
    public String feignTest(){
        return feignDemo.queryIndex();
    }

    @GetMapping("hystrix")
    @HystrixCommand(fallbackMethod = "handleError")
    public String hystrixTest(){
        return feignDemo.hystrix();
    }

    private String handleError() {
        return "error";
    }

}
