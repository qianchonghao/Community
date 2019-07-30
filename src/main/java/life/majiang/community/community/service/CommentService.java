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
import org.springframework.cglib.core.internal.CustomizerRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    @Autowired
    private NotificationService notificationService;
    /*
    commentService 对应commentDTO三个field进行检查，有问题则抛出异常
     */
//    @Transactional//保持下方函数 原子性
//    public void insert(Comment comment, User commentator) {
//
//        if (comment.getParentId() == null || comment.getParentId() == 0) {
//            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
//        }
//        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
//            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
//        }
//
//        if (comment.getType() == CommentTypeEnum.QUESTION.getType()) {
//            //回复问题
//            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
//            if (question == null) throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
//            //增加评论数
//            Question question1 = new Question();
//            question1.setId(comment.getParentId());
//            question1.setCommentCount(1);
//            questionMapperExt.incComment(question1);
//            //增加通知信息
//
//            createNotification(comment, question.getCreator(), NotificationTypeEnum.REPLY_QUESTION.getType(), commentator.getName(), question.getTitle(), question.getId());//ctrl+Alt+m 抽出方法
//        } else {
//            //回复评论
//            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
//            Long outId = getParentQuestionId(comment);
//            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
//            if (dbComment == null) throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
//            //增加一级评论的被评论数
//            Comment temp = dbComment;
//            temp.setCommentCount(1);
//            commentMapperExt.incCommentCount(temp);
//            //增加Notification
//            createNotification(comment, dbComment.getCommentator(), NotificationTypeEnum.REPLY_COMMENT.getType(), commentator.getName(), question.getTitle(), outId);
//        }
//        commentMapper.insert(comment);
//
//    }
    @Transactional
    public void insert(Comment comment, User user) {
        if(comment.getParentId()==null||comment.getParentId()<=0) throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        if(comment.getType()==null||!CommentTypeEnum.isExist(comment.getType())) throw  new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
      //不论type为何，都需要插入db中
        commentMapper.insert(comment);
        if(comment.getType()==CommentTypeEnum.QUESTION.getType()){
            //回复问题
            //实现被回复问题 评论数加1
            Question question = new Question();
            question.setId(comment.getParentId());
            question.setCommentCount(1);
            questionMapperExt.incComment(question);//使用comment打包 id 和commentCount数据 ，进行更新评论数

            //插入notification notificationService?
            Question targetQuestion =questionMapper.selectByPrimaryKey(comment.getParentId());
            //调出被评论的问题，receiver是question.creator
            if(targetQuestion==null) throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
//            User receiver = userMapper.selectByPrimaryKey(question1.getCreator());

            //填写当前评论用户的信息
            notificationService.insert(user.getId(),comment.getParentId(),targetQuestion.getTitle(),targetQuestion.getCreator(),
                    comment.getType(),NotificationTypeEnum.REPLY_QUESTION.getName());

            //填写被评论用户信息 被评论用户的通知数量在interceptor中间，每次count.andReceiverEqual(user.id);输出

        }else if(comment.getType()==CommentTypeEnum.COMMMENT.getType()){
            //回复评论
            //被回复的评论 评论数加1
            Comment comment1 = new Comment();
            comment1.setId(comment.getParentId());
            comment1.setCommentCount(1);
            commentMapperExt.incCommentCount(comment1);

            //notification
            Comment commentParent = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(commentParent==null) throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            Question targetQuestion = questionMapper.selectByPrimaryKey(commentParent.getParentId());
            if(targetQuestion==null) throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);

            notificationService.insert(user.getId(),comment.getParentId(),targetQuestion.getTitle(),targetQuestion.getCreator(),
                    comment.getType(),NotificationTypeEnum.REPLY_COMMENT.getName());
        }
    }

//    private Long getParentQuestionId(Comment comment) {
//        Comment temp = comment;
//        while (temp.getType() != 1) {
//            temp = commentMapper.selectByPrimaryKey(temp.getParentId());
//        }
//        return temp.getParentId();
//    }
//
//    private void createNotification(Comment comment, Long receiver, int notificationType, String notifierName, String outerTitle, Long questionId) {//对着方法Ctrl+F6直接编辑
//
//        Notification notification = new Notification();
//        notification.setGmtCreate(System.currentTimeMillis());
//        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());//创建枚举类为了然阅读者理解 status.value对应的状态
//        notification.setOuterid(questionId);//被评论对象的id
//        notification.setNotifier(comment.getCommentator());//评论对象的创建者id
//        notification.setReceiver(receiver);//被评论对象的创建者
//        //通过ctrl+alt+p 抽取成参数
//        notification.setType(notificationType);
//        notification.setNotifierName(notifierName);
//        notification.setOuterTitle(outerTitle);
//        notificationMapper.insert(notification);
//    }





    public List<CommentDTO> getCommentListById(Long targetId, Integer type) {
        CommentExample example = new CommentExample();
        example.createCriteria().andTypeEqualTo(type).andParentIdEqualTo(targetId);
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);
        //根据 parentId 和type 从db中筛选出comment数据。

        //目的：构建map （Long commentatorID--->User commentator）
        // 原因：comments中部分评论的commentator重复，通过构建map避免逐次userMapper.select,提高效率
        //概论： List<Comments>-->Set<Long>-->List<Long>--->List<User>--->Map<Long,User>
        Set<Long>commentatorIdList0 = comments.stream().map(
                comment -> comment.getCommentator()
        ).collect(Collectors.toSet());//通过set去除重复的commentatorId
        List<Long>commentatorIdList = new ArrayList(commentatorIdList0);//通过set可以直接建立一个list<Long>！！
        List<User>commentators = commentatorIdList.stream()
                .map(commentatorId-> userMapper.selectByPrimaryKey(commentatorId)
                ).collect(Collectors.toList());

        Map<Long,User> commentatorMap = commentators.stream().collect(Collectors.toMap(commentator->commentator.getId(),commentator->commentator));


        List<CommentDTO>commentDTOS =comments.stream()
                .map(comment -> {
                    CommentDTO commentDTO = new CommentDTO();
//                    User user = userMapper.selectByPrimaryKey(comment.getCommentator());
//                    commentDTO.setUser(user);
                    User commentator = commentatorMap.get(comment.getCommentator());
                    commentDTO.setUser(commentator);
                    BeanUtils.copyProperties(comment,commentDTO);
                    return  commentDTO;
                }).collect(Collectors.toList());
        return  commentDTOS;
    }


}
