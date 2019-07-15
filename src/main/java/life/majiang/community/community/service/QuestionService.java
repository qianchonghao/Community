package life.majiang.community.community.service;

import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.QuestionMapperExt;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.QuestionExample;
import life.majiang.community.community.model.User;
import life.majiang.community.community.dto.PageDTO;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//组装的中间层 将questionMapper和UserMapper组装
public class QuestionService {
    @Autowired
    public QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    public QuestionMapperExt questionMapperExt;

    public PageDTO getList(Integer page, Integer size) {
        //size 用來計算 totalPage
        // size*(page -1)
        //offset 和size作用：questionMapper内选取数据库question信息
        Integer totalPage;
        Integer totalCount;


        totalCount= (int)(questionMapper.countByExample(new QuestionExample()));
     //   totalCount= questionMapper.count();

        if(totalCount% size ==0){
            totalPage=totalCount/size;

        }else{
            totalPage=totalCount/size+1;
        }

        if (page < 1) page = 1;
        else if (page > totalPage) page = totalPage;

        Integer offset = size * (page - 1);

        PageDTO pageDTO = new PageDTO();

        List<QuestionDTO> list = new ArrayList<QuestionDTO>();


        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size));
        //getList(offset, size);

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//将同名的变量由 question--->questionDTO
            questionDTO.setUser(user);

            list.add(questionDTO);
        }
        pageDTO.setQuestionDTOList(list);

        pageDTO.setPageInfo(totalPage,page);
        return pageDTO;
    }


    public PageDTO getMyPageDTO(Integer id,Integer page, Integer size) {
        //size 計算 --->totalPage---->关乎ShowEnd等boolean变量设置
        // offset=size*(page -1)
        //offset 和size作用：questionMapper内选取数据库question信息，
        // 此处question信息选取条件是id决定，所以不需要offset
        Integer totalPage;
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(id);
        Integer totalCount = (int)questionMapper.countByExample(example);

        if(totalCount% size ==0){
            totalPage=totalCount/size;

        }else{
            totalPage=totalCount/size+1;
        }

        if(page<1)page=1;

        else if(page>totalPage) page=totalPage;

        Integer offset = size * (page - 1);

        PageDTO pageDTO = new PageDTO();

        List<QuestionDTO> list = new ArrayList<QuestionDTO>();

        QuestionExample example1 = new QuestionExample();
        example1.createCriteria().andCreatorEqualTo(id);
        List<Question> questions=questionMapper.selectByExampleWithRowbounds(example1,new RowBounds(offset,size));

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//将同名的变量由 question--->questionDTO
            questionDTO.setUser(user);

            list.add(questionDTO);
        }
        pageDTO.setQuestionDTOList(list);

        pageDTO.setPageInfo(totalPage,page);
        return pageDTO;
    }

    public QuestionDTO getById(Integer id) {
        //此处id是 question的主键ID 是唯一的。userId对应的是question.creator
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            //
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO =new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return  questionDTO;
    }


    public void updateOrCreate(Question question, Integer questionId) {
        if(questionId!=0){

            question.setGmtModified(System.currentTimeMillis());

            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(questionId);
           int update= questionMapper.updateByExample(question, example);
           if(update==0){
               throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
           }
        }else{
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }
    }

    public void incView(Integer id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionMapperExt.incView(question);
    }
}
