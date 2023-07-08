package com.qst.yunpan.controller;

import com.qst.yunpan.pojo.FileCustom;
import com.qst.yunpan.pojo.Result;
import com.qst.yunpan.pojo.SummaryFile;
import com.qst.yunpan.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
     * @param downPath    下载路径
     * @param username    用户名
     * @return {@link ResponseEntity}<{@link byte[]}>
     */
    @RequestMapping("/download.action")
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

    /**
     * 查找文件（模糊查询）
     *
     * @param request     请求
     * @param reg         文件
     * @param currentPath 当前路径
     * @param regType     文件类型
     * @return {@link Result}<{@link List}<{@link FileCustom}>>
     */
    @RequestMapping("/searchFile.action")
    public @ResponseBody Result<List<FileCustom>> searchFile(HttpServletRequest request, String reg, String currentPath, String regType) {
        try {
            List<FileCustom> searchFile = fileService.searchFile(request, currentPath, reg, regType);
            Result<List<FileCustom>> result = new Result<>(376, true, "查找成功");
            result.setData(searchFile);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(371, false, "查找失败");
        }
    }

//    @RequestMapping("/searchFile")
//    public @ResponseBody Result<List<FileCustom>> searchFile(
//            HttpServletRequest request,
//            String currentPath, String regType) {
//        try {
//            List<FileCustom> searchFile = fileService.searchFile(request, currentPath, regType);
//            Result<List<FileCustom>> result = new Result<>(376, true, "查找成功");
//            result.setData(searchFile);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result<>(371, false, "查找失败");
//        }
//    }

    /**
     * 新建文件夹
     *
     * @param currentPath   当前路径
     * @param directoryName 文件夹名
     * @return Json对象
     */
    @RequestMapping("/addDirectory")
    public @ResponseBody Result<String> addDirectory(HttpServletRequest request, String currentPath, String directoryName) {
        try {
            fileService.addDirectory(request, currentPath, directoryName);
            return new Result<>(336, true, "添加成功");
        } catch (Exception e) {
            return new Result<>(331, false, "添加失败");
        }
    }

    /**
     * 删除文件夹
     *
     * @param currentPath   当前路径
     * @param directoryName 文件夹名
     * @return Json对象
     */
    @RequestMapping("/delDirectory")
    public @ResponseBody Result<String> delDirectory(HttpServletRequest request, String currentPath, String[] directoryName) {
        try {
            fileService.delDirectory(request, currentPath, directoryName);
            return new Result<>(346, true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(341, false, "删除失败");
        }
    }

    /**
     * 重命名文件夹
     *
     * @param currentPath 当前路径
     * @param srcName     源文件名
     * @param destName    目标文件名
     * @return Json对象
     */
    @RequestMapping("/renameDirectory")
    public @ResponseBody Result<String> renameDirectory(HttpServletRequest request, String currentPath, String srcName, String destName) {
        try {
            fileService.renameDirectory(request, currentPath, srcName, destName);
            return new Result<>(356, true, "重命名成功");
        } catch (Exception e) {
            return new Result<>(351, false, "重命名失败");
        }
    }

    @RequestMapping("/summarylist")
    public String summarylist(HttpServletRequest request, Model model) {
        String webrootpath = fileService.getFileName(request, "");
        int number = webrootpath.length();
        SummaryFile rootlist = fileService.summarylistFile(webrootpath, number);
        model.addAttribute("rootlist", rootlist);
        return "summarylist";
    }

    @RequestMapping("/copyDirectory")
    public @ResponseBody Result<String> copyDirectory(HttpServletRequest request, String currentPath, String[] directoryName, String targetdirectorypath) throws Exception {
        try {
            fileService.copyDirectory(request, currentPath, directoryName,
                    targetdirectorypath);
            return new Result<>(366, true, "复制成功");
        } catch (IOException e) {
            return new Result<>(361, true, "复制失败");
        }
    }
}
