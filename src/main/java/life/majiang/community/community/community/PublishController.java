package life.majiang.community.community.community;

import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller

public class PublishController {
    /*
    * 如果是GET访问 则渲染页面
    * 如果是Post访问 则执行请求
    * */
    @Autowired
    private QuestionMapper questionMapper;


    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
    @PostMapping("/publish")
    public  String doPublish(@RequestParam(value = "title",required = false) String title,
                             @RequestParam(value ="description",required = false) String description,
                             @RequestParam(value ="tag",required = false) String tag,
                             @RequestParam(value ="questionId",required = false,defaultValue = "0") String questionIdStr,
                             HttpServletRequest request,
                             Model model)
    {

        Question question = new Question();
        long questionId=Integer.valueOf(questionIdStr);

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        //以上model操作是为了回显到页面
        if(title==null|| title==""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(description==null|| description==""){
            model.addAttribute("error","内容不能为空");
            return "publish";
        }
        if(tag==null|| tag==""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        //提示是否内容填充完整



        User user= (User) request.getSession().getAttribute("user");

        if(user==null){
            model.addAttribute("error","用户未登录");
            //model 内的attribute会和html交互
            return "publish";
        }else {

            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setCreator(user.getId());
            questionService.updateOrCreate(question,questionId);

            return "redirect:/";
        }


    }

    @GetMapping("/publish/{id}")//编辑按钮link至此
    public String edit(@PathVariable("id") long questionId,
                        Model model){
        model.addAttribute("questionId",questionId);
        Question question = questionMapper.selectByPrimaryKey(questionId);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        //此处三个model.addAttribute()是为了实现编辑页面的回显
        model.addAttribute("tag",question.getTag());
        return "publish";//跳转回发布页面？
    }

}
