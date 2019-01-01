package com.pingyougou.manager.controller;

import com.pingyougou.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/upload")
public class UploadController {



    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){

        try {
            //获取文件名
            String originalFilename = file.getOriginalFilename();
            //获取文件扩展名
            String exName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            FastDFSClient fastDFSClient=new FastDFSClient("classpath:config/fdfs_client.conf");
            //基于工具类完成文件上传操作
            String path = fastDFSClient.uploadFile(file.getBytes(), exName);
            //图片访问地址
            String url=FILE_SERVER_URL+path;

            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"图片上传失败");
        }
    }
}
