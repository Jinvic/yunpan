package com.qst.yunpan.controller;

import com.qst.yunpan.pojo.FileCustom;
import com.qst.yunpan.pojo.Result;
import com.qst.yunpan.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileService fileService;

    /**
     * 获取文件列表
     *
     * @param path 路径
     * @return Json对象
     */
    @RequestMapping("/getFiles.action")
    public @ResponseBody Result<List<FileCustom>> getFiles(
            HttpServletRequest request,
            String path) {
        //根据项目路径及用户名、文件名获取上传文件的真实路径
        String realPath = fileService.getFileName(request, path);
        //获取路径下所有的文件信息
        List<FileCustom> listFile = fileService.listFile(realPath);
        //将文件信息封装成Json对象
        Result<List<FileCustom>> result = new Result<List<FileCustom>>
                (325, true, "获取成功");
        result.setData(listFile);
        return result;
    }

    /**
     * 上传
     *
     * @param request     请求
     * @param files       文件
     * @param currentPath 当前路径
     * @return {@link Result}<{@link String}>
     */
    @RequestMapping("/upload.action")
    public @ResponseBody Result<String> upload(
            HttpServletRequest request,
            @RequestParam("files") MultipartFile[] files,
            String currentPath) {
        try {
            fileService.uploadFilePath(request, files, currentPath);
        } catch (Exception e) {
            return new Result<>(301, false, "上传失败");
        }
        return new Result<String>(305, true, "上传成功");
    }

    /**
     * 文件下载
     *
     * @param currentPath 当前路径
     * @param downPath    下路径
     * @param username    用户名
     * @return {@link ResponseEntity}<{@link byte[]}>
     */
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(HttpServletRequest request, String currentPath, String[] downPath, String username) {
        try {
            String down = request.getParameter("downPath");
            File downloadFile = fileService.downPackage(request, currentPath, downPath, username);
            HttpHeaders headers = new HttpHeaders();
            //设置ContentType为二进制数据流
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = new String(downloadFile.getName().getBytes("utf-8"), "iso-8859-1");
            //attachment指示浏览器以附件形式处理响应内容的参数值
            headers.setContentDispositionFormData("attachment", fileName);
            byte[] fileToByteArray = org.apache.commons.io.FileUtils.readFileToByteArray(downloadFile);
            fileService.deleteDownPackage(downloadFile);
            return new ResponseEntity<byte[]>(fileToByteArray, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
