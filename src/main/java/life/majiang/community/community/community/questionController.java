package life.majiang.community.community.community;

import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller

public class questionController {
    //@GetMapping("/question/{id}") 其中value内｛｝包括的内容可以由形参传递


    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(
            @PathVariable(name = "id") Integer id,
            Model model
    ) {
        QuestionDTO questionDTO = questionService.getById(id);
        model.addAttribute("questionDTO",questionDTO);
        questionService.incView(id);
        return "question";
    }
}
