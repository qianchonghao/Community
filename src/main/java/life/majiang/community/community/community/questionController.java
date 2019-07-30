package life.majiang.community.community.community;

import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.dto.PageDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.service.CommentService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller

public class questionController {
    //@GetMapping("/question/{id}") 其中value内｛｝包括的内容可以由形参传递


    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

//    @GetMapping("/question/{id}")
//    public String question(
//            @PathVariable(name = "id") Long questionId,
//            Model model
//    ) {
//        List<CommentDTO> commentDTOS = new ArrayList<CommentDTO>();
//        commentDTOS = commentService.getCommentListById(questionId, CommentTypeEnum.QUESTION.getType());
//        QuestionDTO questionDTO = questionService.getById(questionId);
//        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
//        model.addAttribute("questionDTO", questionDTO);
//        model.addAttribute("commentDTOS", commentDTOS);
//        model.addAttribute("relatedQuestions", relatedQuestions);
//        questionService.incView(questionId);
//
//        return "question";
//    }

    @GetMapping("/question/{id}")
    public String question(
            @PathVariable("id") Long questionId,
            Model model

    ){
        QuestionDTO questionDTO = questionService.getById(questionId);
        //DTO的设置是为了方便和前端的交互，将多个db数据包装在一个bean内。
        questionService.incView(questionId);
        List<Question>relatedQuestions= questionService.getRelatedQuestions(questionDTO);
        //List<Question>形式就能包含html上相关问题模块显示的信息了，所以不必返回L

       List<CommentDTO> commentDTOS = commentService.getCommentListById(
               questionId,CommentTypeEnum.QUESTION.getType());
       //getCommentListById处理回复问题||回复评论的两种情况
        model.addAttribute("questionDTO",questionDTO);
        model.addAttribute("commentDTOS",commentDTOS);
        model.addAttribute("relatedQuestions",relatedQuestions);
        //二级评论展开是有jquery和后端commentController 互动实现的，
        // 通过访问url"/comment/{id}"实现调用CommentController返回JSONString ，而后JQuery利用返回的JSONString搭建html语句并且加载到前端。

        return "question";
    }
}
