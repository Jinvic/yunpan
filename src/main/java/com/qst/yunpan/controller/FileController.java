package com.qst.yunpan.controller;

import com.qst.yunpan.pojo.FileCustom;
import com.qst.yunpan.pojo.RecycleFile;
import com.qst.yunpan.pojo.Result;
import com.qst.yunpan.pojo.SummaryFile;
import com.qst.yunpan.service.FileService;
import com.qst.yunpan.utils.FileUtils;
import com.qst.yunpan.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {
    private static Logger logger = LoggerFactory.getLogger(FileController.class);

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
//            String down = request.getParameter("downPath");
            File downloadFile = fileService.downPackage(request, currentPath, downPath, username);
            HttpHeaders headers = new HttpHeaders();
            //设置ContentType为二进制数据流
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String fileName = new String(downloadFile.getName().getBytes("utf-8"), "iso-8859-1");
            //attachment指示浏览器以附件形式处理响应内容的参数值
            headers.setContentDispositionFormData("attachment", fileName);
            byte[] fileToByteArray = org.apache.commons.io.FileUtils.readFileToByteArray(downloadFile);
            fileService.deleteDownPackage(downloadFile, downPath);
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

    /**
     * 移动文件夹
     *
     * @param currentPath         当前路径
     * @param directoryName       文件夹名
     * @param targetdirectorypath 目标位置
     * @return Json对象
     */
    @RequestMapping("/moveDirectory")
    public @ResponseBody Result<String> moveDirectory(HttpServletRequest request, String currentPath, String[] directoryName, String targetdirectorypath) {
        try {
            fileService.moveDirectory(request, currentPath, directoryName, targetdirectorypath);
            return new Result<>(366, true, "移动成功");
        } catch (Exception e) {
            return new Result<>(361, true, "移动失败");
        }
    }

    /**
     * 打开文件
     *
     * @param response    响应文件流
     * @param currentPath 当前路径
     * @param fileName    文件名
     * @param fileType    文件类型
     */
    @RequestMapping("/openFile")
    public void openFile(HttpServletResponse response, HttpServletRequest request, String currentPath, String fileName, String fileType) {
        try {
            fileService.respFile(response, request, currentPath, fileName, fileType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回视频实际路径
     * 我是傻逼 by Jinvic
     *
     * @param request     请求
     * @param currentPath 当前路径
     * @param fileName    文件名称
     * @return {@link String}
     */
    @RequestMapping("/openVideo")
    public @ResponseBody Result<String> openVideo(HttpServletRequest request, String currentPath, String fileName) {
        try {
            //DEBUG:
            System.out.println("in openVideo");
            System.out.println(currentPath);
            System.out.println(fileName);
            fileService.copyVideoToFileServer(request, currentPath, fileName);
            String filePath = fileService.getVideoPath(request, fileName);
            System.out.println(filePath);
            Result<String> result = new Result<>(555, true, "打开成功");
            result.setData(filePath);
            System.out.println(result.getData());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(777, false, "打开视频失败");
        }
    }

    /**
     * 打开文档
     *
     * @param currentPath 当面路径
     * @param fileName    文件名
     * @param fileType    文件类型
     * @return Json对象（文件Id）
     */
    @RequestMapping("/openOffice")
    public @ResponseBody Result<String> openOffice(HttpServletRequest request, String currentPath,
                                                   String fileName, String fileType) {
        try {
            String openOffice = fileService.openOffice(request, currentPath, fileName);
            if (openOffice != null) {
                Result<String> result = new Result<>(505, true, "打开成功");
                result.setData(openOffice);
                return result;
            }
            return new Result<>(501, false, "打开失败");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(501, false, "打开失败");
        }
    }


    //将路径和文件名传入model，再返回给前台打开
    @RequestMapping("/openAudioPage")
    public String openAudioPage(Model model, String currentPath, String fileName) {
        model.addAttribute("currentPath", currentPath);
        model.addAttribute("fileName", fileName);
        return "audio";
    }

    //获取回收站文件信息
    @RequestMapping("/recycleFile")
    public String recycleFile(HttpServletRequest request) {
        try {
            List<RecycleFile> findDelFile = fileService.recycleFiles(request);
            if (null != findDelFile && findDelFile.size() != 0) {
                request.setAttribute("findDelFile", findDelFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "recycle";
    }

    //将选中的文件进行还原
    @RequestMapping("/revertDirectory")
    public @ResponseBody Result<String> revertDirectory(HttpServletRequest request, int[] fileId) {
        try {
            fileService.revertDirectory(request, fileId);
            return new Result<>(327, true, "还原成功");
        } catch (Exception e) {
            return new Result<>(322, false, "还原失败");
        }
    }

    //清空回收站
    @RequestMapping("/delAllRecycle")
    public @ResponseBody Result<String> delAllRecycleDirectory(HttpServletRequest request) {
        try {
            fileService.delAllRecycle(request);
            // 返回状态码
            return new Result<>(327, true, "删除成功");
        } catch (Exception e) {
            return new Result<>(322, false, "删除失败");
        }
    }

    /**
     * 删除回收站文件
     *
     * @param request 请求
     * @return {@link Result}<{@link String}>
     */
    @RequestMapping("/delRecycle")
    public @ResponseBody Result<String> delRecycleDirectory(HttpServletRequest request, int fileId) {
        try {
            fileService.delRecycle(request, fileId);
            // 返回状态码
            return new Result<>(327, true, "删除成功");
        } catch (Exception e) {
            return new Result<>(322, false, "删除失败");
        }
    }
}
