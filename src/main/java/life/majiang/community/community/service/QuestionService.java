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
        Integer totalPage;
        Integer totalCount = questionMapper.count();

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
}
