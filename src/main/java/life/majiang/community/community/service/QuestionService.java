package life.majiang.community.community.service;

import life.majiang.community.community.dto.PageDTO;
import life.majiang.community.community.dto.QuestionDTO;
import life.majiang.community.community.mapper.QuestionMapper;
import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.Question;
import life.majiang.community.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//组装的中间层 将questionMapper和UserMapper组装
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;



    public PageDTO getList(Integer page, Integer size) {
        //size 用來計算 totalPage
        // size*(page -1)
        //offset 和size作用：questionMapper内选取数据库question信息
        Integer totalPage;
        Integer totalCount = questionMapper.count();

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
        List<Question> questions = questionMapper.getList(offset, size);

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
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
        Integer totalCount = questionMapper.MyCount(id);

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
        List<Question> questions = questionMapper.getListById(id,offset,size);

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
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
        Question question = questionMapper.getById(id);
        User user = userMapper.findById(question.getCreator());
        QuestionDTO questionDTO =new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return  questionDTO;
    }
}
