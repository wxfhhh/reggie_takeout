package com.reggie.controller;

import com.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("common")
public class FileUploadController {
    @Value("${file.path}")
    private String path;
    /**
     * 文件上传
     * @param file  MultipartFile file的参数必须与前端的
     *              payLoad fromData中的name一致
     *              这里可以使用@RequestPart("file") ，这样形参可以随意起名
     * @return
     */
    @PostMapping("upload")
    public Result upload(MultipartFile file) throws IOException {
        log.info("文件上传");
        //获取当前文件名称
        String originalFilename = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = originalFilename.
                substring(originalFilename.lastIndexOf("."));
        //为防止重复使用UUID命名
        String FileName = UUID.randomUUID() + suffix;
        //判断文件夹是否存在
        File file1=new File(path);
        if(!file1.exists())
            file1.mkdirs();
        //将临时文件转存到本地
        file.transferTo(new File(path + FileName));
        //放回原文件名给前端
        return Result.succeed(FileName);
    }

    @GetMapping("download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流读取文件内容
            FileInputStream fis=new FileInputStream(new File(path+name));
            //输出流,将文件内容写入到浏览器 可以通过respond获取输出流
            ServletOutputStream os = response.getOutputStream();
            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fis.read(bytes))!=-1){
                os.write(bytes,0,len);
                os.flush();
            }
            os.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
