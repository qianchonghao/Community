package life.majiang.community.community.community;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.dto.NotificationDTO;
import life.majiang.community.community.enums.NotificationTypeEnum;
import life.majiang.community.community.model.Notification;
import life.majiang.community.community.model.User;
import life.majiang.community.community.service.NotificationService;
import life.majiang.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

//    @GetMapping("/notification/{id}")//一个反斜杠 代表根目录
//    public String Index(HttpServletRequest request,
//                        Model model,
//                        @PathVariable("id") Long notificationId) {
//    //原作者思路：将notification.status的设置交给notificationController 处理，
//    //再跳转至question.html。所以能封装就采用封装，把各个model的db处理交给各自的Controller
//
//        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) return "redirect:/";
//
//        NotificationDTO notificationDTO = notificationService.read(notificationId, user);
//
//
//        if (NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
//                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
//            return "redirect:/question/" + notificationDTO.getOuterid();
//        }//利用返回所得的notificationType 检测是否是该跳转 question.html
//        return "redirect:/";
//    }
    @GetMapping("/notification/{notificationId}")
    public String statusModify(
            @PathVariable("notificationId") Long notificationId,
            HttpServletRequest request
    ){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null) throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        //所有和数据库交互的操作都在service内处理,而Controller内部需要notification.type（必须获得notification）
        // 所以设置notificationService。read()返回NotificationDTO，读取他的type来采取相应的跳转操作
        Notification notification =notificationService.read(notificationId);

        if(NotificationTypeEnum.REPLY_COMMENT.getType()==notification.getType()
        ||NotificationTypeEnum.REPLY_QUESTION.getType()==notification.getType()){
            return "redirect:/question/"+notification.getOuterid();
            //为何必须要用redirect跳转！！！！
            //注意 此处跳转的不是html页面，而是访问url，跳转到后端的questionController
        }
        return  "redirect:/";
    }
}
