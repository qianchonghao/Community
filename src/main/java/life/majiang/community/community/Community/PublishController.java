package life.majiang.community.community.Community;

import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
//@RequestMapping("/publish")
public class PublishController {
    /*
    * 如果是GET访问 则渲染页面
    * 如果是Post访问 则执行请求
    * */
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
    @PostMapping("/publish")
    public  String doPublish(@RequestParam(value = "title",required = false) String title,
                             @RequestParam(value ="description",required = false) String description,
                             @RequestParam(value ="tag",required = false) String tag,
                             HttpServletRequest request,
                             Model model)
    {

        Question question = new Question();
        Cookie[] cookies = request.getCookies();
        User user= null;

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
        if(cookies!=null&&cookies.length>0){
            for(Cookie cookie:cookies){
                /*cookie 由request.getCookies()提供的
                session 由request.getSession.setAttribute()设置属性的
                两者源自request，因而都是后端的信息。
                model session cookie类似于map  是键值对映射
                session mode在html中可取值
                如：session.user 或者 ${error} 可分别取到session和model 对应的key

                */
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    user = userMapper.findByToekn(token);
                    if(user != null){
                        request.getSession().setAttribute("user",user);
                    }

                    break;
                }
            }
        }
        if(user==null){
            model.addAttribute("error","用户未登录");
            //model 内的attribute会和html交互
            return "publish";
        }else{

            question.setTitle(title);
            question.setDescription(description);
            question.setTag(tag);
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCreator(user.getId());


            questionMapper.getQuestion(question);
            return "redirect:/";
        }




    }

}
