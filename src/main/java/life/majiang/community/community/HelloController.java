package life.majiang.community.community;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class HelloController {
    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name",defaultValue = "world") String name, Model model){//ctrl+P会显示 方法所需要的参数
       model.addAttribute("name",name);
       //Model是SpringFrame IO的Model
        //model.add 类似map的键值对
        return "hello";
    }
}
/*整体步骤
1.引入依赖
maven中直接引入

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
2.写HelloController
3.写hello.html文件
 */