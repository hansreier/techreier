package spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

//Note there is a difference between @Controller and @RestController
//@Restcontroller does not work when referencing to thymeleaf templates
//@RestController is for web services.
@Controller
@RequestMapping("/")
public class SimpleWebThymeleafController {

	// inject via application.properties
	@Value("${welcome.message}")
	private String message = "Hello World";

	@RequestMapping({ "/", "/greeting" })
	public String welcome(Model model) {
		model.addAttribute("message", this.message);
		return "welcome";
	}
}
