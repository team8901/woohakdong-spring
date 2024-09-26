package woohakdong.server;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    @CrossOrigin("*")
    public String testAPI() {
        return "Hello, World!";
    }
}
