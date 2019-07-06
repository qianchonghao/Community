package life.majiang.community.community;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class IndexController {//templates中HTML文件名要和control相对应
    @GetMapping("/")//一个反斜杠 代表根目录
    public String Index() {

        return "index";
    }
}
/*
整个IndexController实际只提供了一个页面跳转功能
根据return的String在Templates中寻找对应的HTML文件，从而跳转页面
 */