package life.majiang.community.community.community;

import life.majiang.community.community.Provider.UCloudProvider;
import life.majiang.community.community.dto.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {
    //通过 markdown 图片传输 返回json格式的图片信息并且 访问 /file/upload 跳转至此
    @Autowired
    private UCloudProvider uCloudProvider;

    @ResponseBody
    @RequestMapping("/file/upload")
    public FileDTO upLoad(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");
        // MultipartRequest
        //检查前端页面是靠什么id传递，request转换成multi
        String fileName = null;
        FileDTO fileDTO = new FileDTO();
        try {
            fileName = uCloudProvider.upLoad(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            fileDTO.setSuccess(1);
            fileDTO.setUrl(fileName);//所谓fileName就是文件在云端的url
            return fileDTO;//fileDTO转化成json格式传输回去
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
