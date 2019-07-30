package life.majiang.community.community.service;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.dto.PageDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.dto.QuestionQueryDTO;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.QuestionMapperExt;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.QuestionExample;
import life.majiang.community.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.h2.util.New;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
//组装的中间层 将questionMapper和UserMapper组装
public class QuestionService {
    @Autowired
    public QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    public QuestionMapperExt questionMapperExt;


    public PageDTO<QuestionDTO> getList(String search,Integer page, Integer size){
        //question 列表中 count 和List 都要根据search，在questionMapperExt中进行搜索！！！

        PageDTO<QuestionDTO>pageDTO = new PageDTO<>();

        if(size<1) size =5;

        if(!StringUtils.isBlank(search)){//正则化search
            String[] searchs = search.split(" ");
            search = StringUtils.join(searchs,"|");

        }else{
            search=null;
        }

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        /*
        创建 QuestionQueryDTO这个类的目的：利用search正则形式为条件进行RowBoundsSelect，无法通过example的设置实现。
        因此另行设置MapperExt ，而MapperExt中的方法只能接受一个参数，但是实际上，我们需要search offset size三个参数，所以设计了一个耦合类，
        把这三个信息捆绑在一起进行传递*/


        int totalCount = questionMapperExt.countBySearch(questionQueryDTO);
        int totalPage;
        if(totalCount % size==0) totalPage= Math.toIntExact((totalCount / size));
        else totalPage= Math.toIntExact((totalCount - totalCount % size) / size + 1);

        //处理page数据错误的情况
        if(page<1)page=1;
        else  if(page>totalPage) page= totalPage;

        pageDTO.setPageInfo(Math.toIntExact(totalPage),page);
      //根据size offset search等信息查找相应的questions
        Integer offset = (page-1)*size;

        questionQueryDTO.setOffset(offset);
        questionQueryDTO.setSize(size);

        //QuestionQueryDTO是一个简单的bean ，聚合了search offset size
        List<Question> questions=questionMapperExt.selectBySearchWithRowbounds(questionQueryDTO);
        //用questionMapperExt 内的方法替代questionMapper.selectByRowBounds()达到增加search筛选条件的效果

//        QuestionExample example1 = new QuestionExample();
//        example1.setOrderByClause("gmt_create DESC");//注意添加order ，使得按照gmt_create递减生成
//        List<Question> questions= questionMapper.selectByExampleWithRowbounds(example1,new RowBounds(offset,size));

        List<QuestionDTO>questionDTOS= questions.stream().map(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            return questionDTO;
        }).collect(Collectors.toList());

        pageDTO.setDatas(questionDTOS);
        return pageDTO;
    }

//    public PageDTO getMyPageDTO(Long id, Integer page, Integer size) {
//        //size 計算 --->totalPage---->关乎ShowEnd等boolean变量设置
//        // offset=size*(page -1)
//        //offset 和size作用：questionMapper内选取数据库question信息，
//        // 此处question信息选取条件是id决定，所以不需要offset
//        Integer totalPage;
//        QuestionExample example = new QuestionExample();
//        example.createCriteria().andCreatorEqualTo(id);
//        Integer totalCount = (int) questionMapper.countByExample(example);
//
//        if (totalCount % size == 0) {
//            totalPage = totalCount / size;
//
//        } else {
//            totalPage = totalCount / size + 1;
//        }
//
//        if (page < 1) page = 1;
//
//        else if (page > totalPage) page = totalPage;
//
//        Integer offset = size * (page - 1);
//
//        PageDTO<QuestionDTO> pageDTO = new PageDTO<QuestionDTO>();
//
//        List<QuestionDTO> list = new ArrayList<QuestionDTO>();
//
//        QuestionExample example1 = new QuestionExample();
//        example1.createCriteria().andCreatorEqualTo(id);
//        example1.setOrderByClause("gmt_create DESC");
//        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));
//
//        for (Question question : questions) {
//            User user = userMapper.selectByPrimaryKey(question.getCreator());
//            QuestionDTO questionDTO = new QuestionDTO();
//            BeanUtils.copyProperties(question, questionDTO);//将同名的变量由 question--->questionDTO
//            questionDTO.setUser(user);
//
//            list.add(questionDTO);
//        }
//        pageDTO.setDatas(list);
//
//        pageDTO.setPageInfo(totalPage, page);
//        return pageDTO;
//    }

    public PageDTO<QuestionDTO> getMyPageDTO(Long userId,Integer page,Integer size){
        PageDTO<QuestionDTO>pageDTO = new PageDTO<>();
        if(size<1) size =2;

        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        Long totalCount = questionMapper.countByExample(example);
        Integer totalPage;
        if(totalCount % size==0) totalPage= Math.toIntExact((totalCount / size));
        else totalPage= Math.toIntExact((totalCount - totalCount % size) / size + 1);

        if(page<1)page=1;
        else if(page>totalPage) page = totalPage;

        pageDTO.setPageInfo(totalPage,page);
        int offset = (page -1)*size;
        QuestionExample example1 = new QuestionExample();
        example1.createCriteria().andCreatorEqualTo(userId);
        example1.setOrderByClause("gmt_create desc");
        List<Question>questions = questionMapper.selectByExampleWithRowbounds(example1,new RowBounds(offset,size));
        List<QuestionDTO>questionDTOS = questions.stream().map(
                question -> {
                    QuestionDTO questionDTO = new QuestionDTO();
                    BeanUtils.copyProperties(question,questionDTO);
                    User user = userMapper.selectByPrimaryKey(userId);
                    questionDTO.setUser(user);
                    return questionDTO;
                }
        ).collect(Collectors.toList());

        pageDTO.setDatas(questionDTOS);
        return pageDTO;
    }


//    public QuestionDTO getById(Long id) {
//        //此处id是 question的主键ID 是唯一的。userId对应的是question.creator
//        Question question = questionMapper.selectByPrimaryKey(id);
//        if (question == null) {
//            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
//            //
//        }
//        User user = userMapper.selectByPrimaryKey(question.getCreator());
//        QuestionDTO questionDTO = new QuestionDTO();
//        BeanUtils.copyProperties(question, questionDTO);
//        questionDTO.setUser(user);
//        return questionDTO;
//    }

     public QuestionDTO getById(Long questionId){
        QuestionDTO questionDTO = new QuestionDTO();

        Question question = questionMapper.selectByPrimaryKey(questionId);
         if(question==null){
             throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
         }
        User creator = userMapper.selectByPrimaryKey(question.getCreator());


        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(creator);
        return  questionDTO ;
     }
    public void updateOrCreate(Question question, Long questionId) {
        if (questionId != 0) {



            Question temp = questionMapper.selectByPrimaryKey(questionId);

            question.setId(temp.getId());
            question.setViewCount(temp.getViewCount());
            question.setCommentCount(temp.getCommentCount());
            question.setLikeCount(temp.getLikeCount());
            question.setGmtCreate(temp.getGmtCreate());

            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(questionId);
            int update = questionMapper.updateByExample(question, example);
            if (update == 0) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        } else {

            question.setLikeCount(0);
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setGmtCreate(question.getGmtModified());

            questionMapper.insert(question);
        }
    }

//    public void incView(Long id) {
//
//        Question question = new Question();
//        question.setId(id);
//        question.setViewCount(1);
//        questionMapperExt.incView(question);
//    }
    public void incView(Long questionId){
        Question question = new Question();
        question.setId(questionId);
        question.setViewCount(1);
        questionMapperExt.incView(question);

    }

//    public List<QuestionDTO> selectRelated(QuestionDTO questionDTO) {
//        if (StringUtils.isBlank(questionDTO.getTag())) {
//            return new ArrayList<>();
//        } else {
//
//
////            String[] tags=StringUtils.split(questionDTO.getTag(),',');
////            String regexTag=Arrays.stream(tags).collect(Collectors.joining("|"));
//            String[] tags = StringUtils.split(questionDTO.getTag(), ',');
//            List<String> list = Arrays.asList(tags);
//            String regexTag = list.stream().collect(Collectors.joining("|"));
//            //delimeter 分隔符
//            Question question = new Question();
//            question.setId(questionDTO.getId());
//            question.setTag(regexTag);//通過StringJoiner 将tag转化成regex形式，然后set到参数question
//            List<Question> questions = questionMapperExt.selectRelated(question);
//            List<QuestionDTO> questionDTOS = questions.stream().map(tempQuestion -> {
//                QuestionDTO tempQuestionDTO = new QuestionDTO();
//                BeanUtils.copyProperties(tempQuestion, tempQuestionDTO);
//                //copyProperties(src，editable)
//                tempQuestionDTO.setUser(userMapper.selectByPrimaryKey(tempQuestion.getCreator()));
//                return tempQuestionDTO;
//                //user 是questionCreator 的具体用户信息
//            }).collect(Collectors.toList());
//            return questionDTOS;
//
//        }
//
//    }


    public Long questionCount(Long userId) {

        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        return questionMapper.countByExample(example);
    }

    public List<Question> getRelatedQuestions(QuestionDTO questionDTO) {
        String tag = questionDTO.getTag();
        if(StringUtils.isBlank(tag)){//标签为空，则返回一个空list
            return new ArrayList<Question>();
        }else{
            String[] temp =tag.split(",");
            String tagRegex= StringUtils.join(temp,"|");
            Question question = new Question();
            question.setTag(tagRegex);
            question.setId(questionDTO.getId());
            List<Question>questions = questionMapperExt.selectRelated(question);
            return questions;
        }
    }
}

