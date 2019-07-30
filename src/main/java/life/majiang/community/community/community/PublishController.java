package life.majiang.community.community.community;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.cache.TagCache;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
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
    public String publish(Model model) {
        model.addAttribute("tagDTOS", TagCache.get());
        return "publish";
    }



    @PostMapping("/publish")
   public  String publish(
           @RequestParam("title") String title,
           @RequestParam("description") String description,
           @RequestParam("tag") String tag,
           @RequestParam(value = "questionId" ,required = false,defaultValue = "0") Long questionId,
           //questionId用于识别 publish中 createOrUpdate. questionId是通过model.addAttribute &&submit提交实现传递功能的
           Model model,
           HttpServletRequest request
    ){
        model.addAttribute("tagDTOS",TagCache.get());//实现html页面上 标签选项的映射
        StringBuilder error = new StringBuilder();
        User user =(User) request.getSession().getAttribute("user");
        //检查user是否登录！！
        if(user==null){
            error.append("用户未登录");
            return  "publish";
        }


        if(StringUtils.isBlank(title)){
            error.append("标题不能为空! ");
        }else{
            model.addAttribute("title",title);
        }
        if(StringUtils.isBlank(description)){
            error.append("内容不能为空！ ");
        }else{
            model.addAttribute("description",description);
        }
        if(StringUtils.isBlank(tag)){
            error.append("标签不能为空! ");
        }else{
            model.addAttribute("tag",tag);
        }

        if(!TagCache.isValid(tag)){
            error.append("标签不规范! ");
        }

        if(error.length()>0){
            model.addAttribute("error",error);
            return "publish";
        }





        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtModified(System.currentTimeMillis());
        //        question.setGmtCreate();
        //        question.setLikeCount();
        //        question.setCommentCount();
        //        question.setViewCount();
        //上面的注释掉的 Field是在creteOrUpdate进行分类设置
        questionService.updateOrCreate(question,questionId);
        return "redirect:/";
    }

    @GetMapping("/publish/{id}")
    public String modify(
            @PathVariable("id")Long id,
            Model model

    ){
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        model.addAttribute("tagDTOS",TagCache.get());//实现html页面上 标签选项的映射
        //以下三者实现 问题信息回显
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        //实现input type=hidden 实现编辑submit 中提交questionId数据，从而区分createOrUpdate
        model.addAttribute("questionId",id);

        return "publish";
    }
//    @PostMapping("/publish")
//    public String doPublish(@RequestParam(value = "title", required = false) String title,
//                            @RequestParam(value = "description", required = false) String description,
//                            @RequestParam(value = "tag", required = false) String tag,
//                            @RequestParam(value = "questionId", required = false, defaultValue = "0") String questionIdStr,
//                            HttpServletRequest request,
//                            Model model) {
//
//        Question question = new Question();
//        long questionId = Integer.valueOf(questionIdStr);
//
//        model.addAttribute("title", title);
//        model.addAttribute("description", description);
//        model.addAttribute("tag", tag);
//        model.addAttribute("tagDTOS", TagCache.get());
//        //以上model操作是为了回显到页面
//        if (title == null || title == "") {
//            model.addAttribute("error", "标题不能为空");
//            return "publish";
//        }
//        if (description == null || description == "") {
//            model.addAttribute("error", "内容不能为空");
//            return "publish";
//        }
//        if (tag == null || tag == "") {
//            model.addAttribute("error", "标签不能为空");
//            return "publish";
//        }
//        //提示是否内容填充完整
//
//        if (!TagCache.isValid(tag)) {
//            model.addAttribute("error", "輸入非法标签");
//            return "publish";
//        }
//
//        User user = (User) request.getSession().getAttribute("user");
//
//        if (user == null) {
//            model.addAttribute("error", "用户未登录");
//            //model 内的attribute会和html交互
//            return "publish";
//        } else {
//
//            question.setTitle(title);
//            question.setDescription(description);
//            question.setTag(tag);
//            question.setCreator(user.getId());
//            questionService.updateOrCreate(question, questionId);
//
//            return "redirect:/";
//        }
//
//
//    }

//    @GetMapping("/publish/{id}")//编辑按钮link至此
//    public String edit(@PathVariable("id") long questionId,
//                       Model model) {
//        model.addAttribute("tagDTOS", TagCache.get());
//        model.addAttribute("questionId", questionId);
//        Question question = questionMapper.selectByPrimaryKey(questionId);
//        model.addAttribute("title", question.getTitle());
//        model.addAttribute("description", question.getDescription());
//        //此处三个model.addAttribute()是为了实现编辑页面的回显
//        model.addAttribute("tag", question.getTag());
//        return "publish";//跳转回发布页面？
//    }

}
