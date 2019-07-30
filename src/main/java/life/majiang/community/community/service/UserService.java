package life.majiang.community.community.service;

import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    //DataBase 數據庫中 user含有accountID 是关联github账号的id ，具有唯一性


    public void createOrUpdate(User user) {

        UserExample example = new UserExample();
        example.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> userList = userMapper.selectByExample(example);
        User temp = userList.get(0);
        // User temp = userMapper.getUserByAccountId(user.getAccountId());
        if (temp != null) {
            //如果user存在，那么gmtCreate()是不会变化的，仅setModified
            UserExample example1 = new UserExample();
            example1.createCriteria().andAccountIdEqualTo(user.getAccountId());
            userMapper.updateByExampleSelective(user, example1);

        } else {
            //如果gitUser之前不存在，那么上传
            user.setGmtCreate(System.currentTimeMillis());

            userMapper.insert(user);
        }
    }



}
