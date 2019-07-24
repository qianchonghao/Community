package life.majiang.community.community.service;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.dto.CommentDTO;
import life.majiang.community.community.enums.CommentTypeEnum;
import life.majiang.community.community.enums.NotificationStatusEnum;
import life.majiang.community.community.enums.NotificationTypeEnum;
import life.majiang.community.community.mapper.*;
import life.majiang.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionMapperExt questionMapperExt;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapperExt commentMapperExt;
    @Autowired
    private NotificationMapper notificationMapper;

/*
commentService 对应commentDTO三个field进行检查，有问题则抛出异常
 */
@Transactional//保持下方函数 原子性
public void insert(Comment comment,User commentator) {

    if (comment.getParentId() == null || comment.getParentId() == 0) {
        throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
    }
    if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
        throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
    }

    if (comment.getType() == CommentTypeEnum.QUESTION.getType()) {
        //回复问题
        Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
        if (question == null) throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
       //增加评论数
        Question question1 = new Question();
        question1.setId(comment.getParentId());
        question1.setCommentCount(1);
        questionMapperExt.incComment(question1);
        //增加通知信息

        createNotification(comment, question.getCreator(), NotificationTypeEnum.REPLY_QUESTION.getType(), commentator.getName(),question.getTitle(), question.getId());//ctrl+Alt+m 抽出方法
    } else {
        //回复评论
        Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
        Long outId=getParentQuestionId(comment);
        Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
        if (dbComment == null) throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
        //增加一级评论的被评论数
        Comment temp = dbComment;
        temp.setCommentCount(1);
        commentMapperExt.incCommentCount(temp);
        //增加Notification
        createNotification(comment, dbComment.getCommentator(), NotificationTypeEnum.REPLY_COMMENT.getType(), commentator.getName(),question.getTitle(), outId);
    }
    commentMapper.insert(comment);

}

    private Long getParentQuestionId(Comment comment) {
        Comment temp =comment;
        while(temp.getType()!=1){
            temp=commentMapper.selectByPrimaryKey(temp.getParentId());
        }
        return temp.getParentId();
    }

    private void createNotification(Comment comment, Long receiver, int notificationType, String notifierName, String outerTitle, Long questionId) {//对着方法Ctrl+F6直接编辑

        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());//创建枚举类为了然阅读者理解 status.value对应的状态
        notification.setOuterid(questionId);//被评论对象的id
        notification.setNotifier(comment.getCommentator());//评论对象的创建者id
        notification.setReceiver(receiver);//被评论对象的创建者
        //通过ctrl+alt+p 抽取成参数
        notification.setType(notificationType);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }



    public List<CommentDTO> getCommentListById(Long targetId, Integer type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().
                andParentIdEqualTo(targetId).
                andTypeEqualTo(type);
        //Ctrl+Alt+p 抽取参数
        //Ctrl+Alt+v 抽取变量
        //Ctrl+Alt+i 抽取方法 hgtyuiopoiuytrew

        commentExample.setOrderByClause("gmt_create DESC");
        //写入关键字 递增排序为 ASC 递减排序为DESC
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        //获取去重的评论人
        Set<Long> commentators =
                comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());

        List<Long>userId = new ArrayList<Long>(commentators);



        //获取评论人 并转化为MAP
        UserExample example = new UserExample();
        example.createCriteria().andIdIn(userId);//根据容器内value来挑选数据库内数据

        List<User>users =userMapper.selectByExample(example);
//        Map<Long,User> temp = users.stream().collect(Collectors.toMap(u->u.getId(),u->u));

        /* list等集合类型 都可以使用 stream()功能
        stream()不会修改数据源，将操作后的数据存储到另一个对象中
         Stream<String> s1 = userList.stream().map(user->user.getId());//map返回一个数据类型为Long内容为ID的stream
        Set<Long> commentator = userList.stream().map(user->user.getId()).collect(Collectors.toSet())
        末尾添加的collect 根据map返回的Stream<Long> 返回一个对应的Set
        Map<Long,User> userMap = userList.stream.collect(Collectors.toMap(user->userId,user->user))
        */
      List<CommentDTO>commentDTOS = new ArrayList<>();
        for( Comment comment:comments){
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            User user = userMapper.selectByPrimaryKey(comment.getCommentator());
            commentDTO.setUser(user);
            commentDTOS.add(commentDTO);
        }
//    lambda转换 注意转换式内部最后return

//        List<CommentDTO>commentDTOS = comments.stream().map(
//                comment -> {
//                    CommentDTO commentDTO = new CommentDTO();
//                    BeanUtils.copyProperties(comment,commentDTO);
//                    commentDTO.setUser(temp.get(comment.getCommentator()));
//                    return commentDTO;
//                }
//        ).collect(Collectors.toList());
        return commentDTOS;
    }
}
