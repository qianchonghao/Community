package life.majiang.community.community.community;

import life.majiang.community.community.dto.PageDTO;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.NotificationService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
//questionDTO 比question 多存储一个User信息
@Controller
public class profileController {


    @Autowired
    QuestionService questionService;
    @Autowired
    private NotificationService notificationService;
    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "2") Integer size
    ) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) return "redirect:/";

        if ("question".equals(action)) {//避免action为空
            model.addAttribute("section", "question");
            model.addAttribute("sectionName", "我的提问");
            PageDTO pageDTO = questionService.getMyPageDTO(user.getId(), page, size);
            Long questionCount = questionService.questionCount(user.getId());
            model.addAttribute("questionCount",questionCount);
            model.addAttribute("pageDTO", pageDTO);
        }
        if ("replies".equals(action)) {//避免action为空
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");

            PageDTO pageDTO = notificationService.getNotificationPage(user.getId(), page, size);
            model.addAttribute("pageDTO", pageDTO);


        }



        return "profile";
    }
}
