package life.majiang.community.community.community;

import life.majiang.community.community.dto.NotificationDTO;
import life.majiang.community.community.enums.NotificationTypeEnum;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.NotificationService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;
    @GetMapping("/notification/{id}")//一个反斜杠 代表根目录
    public String Index(HttpServletRequest request,
                        Model model,
                        @PathVariable("id") Long notificationId) {


        User user = (User)request.getSession().getAttribute("user");
        if(user==null) return "redirect:/";

       NotificationDTO notificationDTO= notificationService.read(notificationId,user);


        if(NotificationTypeEnum.REPLY_COMMENT.getType()==notificationDTO.getType()
        || NotificationTypeEnum.REPLY_QUESTION.getType()==notificationDTO.getType()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }
        return "redirect:/";
    }
}
