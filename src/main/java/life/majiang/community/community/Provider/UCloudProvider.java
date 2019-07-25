package life.majiang.community.community.Provider;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.BucketAuthorization;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileBucketLocalAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import life.majiang.community.community.Exception.CustomizeErrorCode;
import life.majiang.community.community.Exception.CustomizeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UCloudProvider {
    @Value("${ucloud.ufile.public-key}")
    private String publicKey;

    @Value("${ucloud.ufile.private-key}")
    private String privateKey;//spring实例化时，才会赋值 @Value key

    @Value("${ucloud.ufile.bucket-name}")
    private String bucketName;

    @Value("${ucloud.ufile.region}")
    private String region;

    @Value("${ucloud.ufile.suffix}")
    private String suffix;

    @Value("${ucloud.ufile.expiresDuration}")
    private Integer expiresDuration;

    public String upLoad(InputStream fileInputStream, String mimeType, String fileName) {
        //三个参数分别为 文件输入流，文件contentType，文件名

        ObjectAuthorization objectAuthorization = new UfileObjectLocalAuthorization(
                publicKey, privateKey);

        ObjectConfig config = new ObjectConfig(region, suffix);
        //config需要 地域和域名作为参数

        String generatorFileName;
        String[] filePaths = fileName.split("\\.");//split需要转义字符
        if (filePaths.length > 1) {
            generatorFileName = UUID.randomUUID().toString() + "." + filePaths[filePaths.length - 1];
        } else {
            throw new CustomizeException(CustomizeErrorCode.File_UPLOAD_FAILED);
        }


        try {

            PutObjectResultBean response = UfileClient.object(objectAuthorization, config)
                    .putObject(fileInputStream, mimeType)//考虑putObject(InputStream,) 流的方式
                    .nameAs(generatorFileName)
                    .toBucket(bucketName)

                    .setOnProgressListener((bytesWritten, contentLength) -> {
                    })
                    .execute();//上传文件


            if (response != null && response.getRetCode() == 0) {//为了上传文件之后
                String url = UfileClient.object(objectAuthorization, config)
                        .getDownloadUrlFromPrivateBucket(generatorFileName, bucketName, expiresDuration)
                        .createUrl();//getDownLoadFromBucket 是利用
                return url;
            } else {
                throw new CustomizeException(CustomizeErrorCode.File_UPLOAD_FAILED);
            }
        } catch (UfileClientException e) {
            throw new CustomizeException(CustomizeErrorCode.File_UPLOAD_FAILED);
        } catch (UfileServerException e) {
            throw new CustomizeException(CustomizeErrorCode.File_UPLOAD_FAILED);
        }

    }


}
