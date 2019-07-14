package life.majiang.community.community;


import life.majiang.community.dto.PageDTO;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller

public class IndexController {//templates中HTML文件名要和control相对应


    @Autowired
    private QuestionService questionService;
    @GetMapping("/")//一个反斜杠 代表根目录
    public String Index(HttpServletRequest request,
                        Model model,
                        @RequestParam(value="page",defaultValue = "1")Integer page,
                        @RequestParam(value="size",defaultValue = "5")Integer size) {


            /*cookies都是存储在前端的数据 当第二次访问时候，第一次已经通过UUID.randomUUID()在前端存储{name=“token”}的cookie。
            所以采取遍历，如果不是第一次访问，那么必然存在{name=“token”}cookie。再根据cookie的value是否和数据库存储的
            之前cookie value的Field（token）相符合 ，如果符合直接跳转至登录完成的页面。否则跳转至待登录页面。
            */
            //?为什么会跳转到这些代码



       PageDTO pageDTO = questionService.getList(page,size);

        model.addAttribute("pageDTO",pageDTO);

        return "index";
    }

}
/*
整个IndexController实际只提供了一个页面跳转功能
根据return的String在Templates中寻找对应的HTML文件，从而跳转页面
 */