package antmedia.loadtester;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
@EnableAutoConfiguration
public class App extends WebMvcConfigurerAdapter{

   
    @GetMapping("/config")
    @ResponseBody
    String getConfig() {
    	return ConfigManager.getConfig();
    }
    
    @PostMapping("/config")
    @ResponseBody
    String setConfig(@RequestBody String confs) {
    	ConfigManager.setConfig(confs);
    	return "OK";
    }
    
    @GetMapping("/test")
    @ResponseBody
    String test(@RequestParam("type") String type) {
    	TestRunner.runTest(type);
    	return "OK";
    }
    
    @GetMapping("/results")
    @ResponseBody
    String getResults() {
    	return TestRunner.getHistory();
    }
    
    @GetMapping("/isTestFinished")
    @ResponseBody
    String getRunningTest() {
    	return TestRunner.getIsTestFinished()+"";
    }
    
    
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/testresults/**").addResourceLocations("file:/home/antmedia/test/results/");
    }
    
    
	public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }
}