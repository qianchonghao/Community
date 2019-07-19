package life.majiang.community.community.community;

import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
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

    @GetMapping("/question/{id}")
    public String question(
            @PathVariable(name = "id") Long questionId,
            Model model
    ) {
        List<CommentDTO> commentDTOS = new ArrayList<CommentDTO>();
        commentDTOS = commentService.getCommentListById(questionId, CommentTypeEnum.QUESTION.getType());

        QuestionDTO questionDTO = questionService.getById(questionId);
        model.addAttribute("questionDTO",questionDTO);
        model.addAttribute("commentDTOS",commentDTOS);
        questionService.incView(questionId);

        return "question";
    }
}
