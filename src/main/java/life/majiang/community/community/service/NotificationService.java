package life.majiang.community.community.service;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.dto.NotificationDTO;
import life.majiang.community.community.dto.PageDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.enums.NotificationStatusEnum;
import life.majiang.community.community.enums.NotificationTypeEnum;
import life.majiang.community.community.mapper.NotificationMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

    public PageDTO getNotificationPage(Long userId, Integer page, Integer size) {
        Integer totalPage;
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(example);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;

        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) page = 1;

        else if (page > totalPage) page = totalPage;

        Integer offset = size * (page - 1);

        PageDTO<NotificationDTO> pageDTO = new PageDTO<>();


        NotificationExample example1 = new NotificationExample();
        example1.createCriteria().andReceiverEqualTo(userId);
        example1.setOrderByClause("gmt_create DESC");

        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));


        if (notifications.size() == 0) return pageDTO;


        Set<Long> notifyId = notifications.stream().map(Notification::getNotifier).collect(Collectors.toSet());
        //利用set去除重复 nitifyUser信息
        List<Long> notifyId2 = new ArrayList<>(notifyId);//通过ArrayList(Set)直接将Set-->List
        UserExample example2 = new UserExample();
        example2.createCriteria().andIdIn(notifyId2);
        List<User> notifyUser = userMapper.selectByExample(example2);
        Map<Long, User> notifyUserMap = notifyUser.stream().collect(Collectors.toMap(u -> u.getId(), u -> u));

        List<NotificationDTO> notificationDTOS = notifications.stream().map(
                notification -> {
                    NotificationDTO notificationDTO = new NotificationDTO();
                    BeanUtils.copyProperties(notification, notificationDTO);//将同名的变量由 question--->questionDTO
                    notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

                    return notificationDTO;
                }
        ).collect(Collectors.toList());

//        for (Notification notification : notifications) {
//            User notifyUser = userMapper.selectByPrimaryKey(notification.getNotifier());
//            NotificationDTO notificationDTO = new NotificationDTO();
//            BeanUtils.copyProperties(notification, notificationDTO);//将同名的变量由 question--->questionDTO
//            notificationDTO.setNotifyUser(notifyUser);
//            notificationDTOS.add(notificationDTO);
//        }

        pageDTO.setDatas(notificationDTOS);

        pageDTO.setPageInfo(totalPage, page);

        return pageDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(0);
        return notificationMapper.countByExample(example);
    }

    public NotificationDTO read(Long notificationId, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        if (!notification.getReceiver().equals(user.getId())) {//为什么只能用equals比较 不能用！=比较
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAILED);
        }
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);//将同名的变量由 question--->questionDTO
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));


        return notificationDTO;
    }
}
