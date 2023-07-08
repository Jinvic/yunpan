package com.qst.yunpan.service;

import com.qst.yunpan.dao.OfficeDao;
import com.qst.yunpan.dao.UserDao;
import com.qst.yunpan.pojo.FileCustom;
import com.qst.yunpan.pojo.RecycleFile;
import com.qst.yunpan.pojo.User;
import com.qst.yunpan.utils.FileUtils;
import com.qst.yunpan.utils.UserUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * 文件业务逻辑类
 */
@Service
public class FileService {
    private Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private OfficeDao officeDao;
    @Autowired
    private UserDao userDao;


    //文件相对前缀
    public static final String PREFIX = "WEB-INF" + File.separator + "file" + File.separator;
    //新用户注册默认文件夹
    protected static final String[] DEFAULT_DIRECTORY = {"vido", "music", "source", "image", User.RECYCLE};

    //给注册用户开辟网盘空间
    public void addNewNameSpace(HttpServletRequest request, String namespace) {
        String fileName = getRootPath(request);
        File file = new File(fileName, namespace);
        file.mkdir();
        for (String newFileName : DEFAULT_DIRECTORY) {
            File newFile = new File(file, newFileName);
            newFile.mkdir();
        }
    }

    //获取网盘根路径
    public String getRootPath(HttpServletRequest request) {
        String rootPath = request.getSession().getServletContext().getRealPath("/") + PREFIX;
        return rootPath;
    }

    /**
     * 获取文件路径
     * (那你为什么写name by Jinvic）
     *
     * @param request  请求
     * @param fileName 文件名称
     * @return {@link String}
     */
    public String getFileName(HttpServletRequest request, String fileName) {

        if (fileName == null || fileName.equals("\\")) {
            System.out.println(1);
            fileName = "";
        } else {
            fileName = fileName.replace("\\", "//");
        }
        String username = UserUtils.getUsername(request);
        String realpath = getRootPath(request) + username + File.separator + fileName;
        return realpath;
    }

    /**
     * 根据用户名获取文件路径
     *
     * @param request  请求
     * @param fileName 文件名称
     * @param username 用户名
     * @return {@link String}
     */
    public String getFileName(HttpServletRequest request, String fileName, String username) {
        if (username == null) {
            return getFileName(request, fileName);
        }
        if (fileName == null) {
            fileName = "";
        }
        return getRootPath(request) + username + File.separator + fileName;
    }

    /**
     * 列出文件
     *
     * @param realPath 真正路径
     * @return {@link List}<{@link FileCustom}>
     */
    public List<FileCustom> listFile(String realPath) {
        File[] files = new File(realPath).listFiles();
        System.out.println("+++++++++++++++++++++++++");
        List<FileCustom> lists = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().equals(User.RECYCLE)) {
                    FileCustom custom = new FileCustom();
                    custom.setFileName(file.getName());
                    custom.setLastTime(FileUtils.formatTime(file.lastModified()));
                    custom.setCurrentPath(realPath);
                    if (file.isDirectory()) {
                        custom.setFileSize("-");
                    } else {
                        custom.setFileSize(FileUtils.getDataSize(file.length()));
                    }
                    custom.setFileType(FileUtils.getFileType(file));
                    lists.add(custom);
                    System.out.println(custom + "----------------");
                }
            }
        }
        return lists;
    }

    /**
     * 上传文件路径
     *
     * @param request     请求
     * @param files       文件
     * @param currentPath 当前路径
     * @throws Exception 异常
     */
    public void uploadFilePath(HttpServletRequest request, MultipartFile[] files, String currentPath) throws Exception {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String filePath = getFileName(request, currentPath);
            File distFile = new File(filePath, fileName);
            if (!distFile.exists()) {
                file.transferTo(distFile);
                if ("office".equals(FileUtils.getFileType(distFile))) {
                    try {
                        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
                        String documentId = FileUtils.getDocClient().createDocument(distFile, fileName, suffix).getDocumentId();
                        officeDao.addOffice(documentId, FileUtils.MD5(distFile));
                    } catch (Exception e) {
                    }
                }
            }
        }
        reSize(request);
    }

    /**
     * 重新计算大小
     *
     * @param request 请求
     */
    public void reSize(HttpServletRequest request) {
        String userName = UserUtils.getUsername(request);
        try {
            userDao.reSize(userName, countFileSize(request));
        } catch (Exception e) {
            logger.error(e.toString());
//            e.printStackTrace();
        }
    }

    /**
     * 统计用户文件大小
     *
     * @param request 请求
     * @return {@link String}
     */
    public String countFileSize(HttpServletRequest request) {
        long countFileSize = countFileSize(new File(getFileName(request, null)));
        return FileUtils.getDataSize(countFileSize);
    }

    private long countFileSize(File srcFile) {
        File[] listFiles = srcFile.listFiles();
        if (listFiles == null) {
            return 0;
        }
        long count = 0;
        for (File file : listFiles) {
            if (file.isDirectory()) {
                count += countFileSize(file);
            } else {
                count += file.length();
            }
        }
        return count;
    }

    /**
     * 下载文件
     *
     * @param request     请求
     * @param currentPath 当前路径
     * @param fileNames   文件名
     * @param username    用户名
     * @return {@link File}
     * @throws Exception 异常
     */
    public File downPackage(HttpServletRequest request, String currentPath, String[] fileNames, String username) throws Exception {
        File downloadFile = null;
        if (currentPath == null) {
            currentPath = "";
        }
        //单文件length为1
        if (fileNames.length == 1) {
            downloadFile = new File(getFileName(request, currentPath, username), fileNames[0]);
            if (downloadFile.isFile()) {
                return downloadFile;
            }
        }
        String[] sourcePath = new String[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            sourcePath[i] = getFileName(request, currentPath, username) + File.separator + fileNames[i];
        }
        String packageZipName = packageZip(sourcePath);
        downloadFile = new File(packageZipName);
        return downloadFile;
    }

    /**
     * 压缩文件
     *
     * @param sourcePath 源路径
     * @return {@link String}
     * @throws Exception 异常
     */
    private String packageZip(String[] sourcePath) throws Exception {
        String zipName = sourcePath[0] + (sourcePath.length == 1 ? "" : "等" + sourcePath.length + "个文件") + ".zip";
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipName));
            for (String string : sourcePath) {
                writeZos(new File(string), "", zos);
            }
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
        return zipName;
    }

    /**
     * 写入文件到压缩包
     *
     * @param file     文件
     * @param basePath 基本路径
     * @param zos      ZipOutputStream
     * @throws IOException ioexception
     */
    private void writeZos(File file, String basePath, ZipOutputStream zos) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles.length != 0) {
                for (File childFile : listFiles) {
                    writeZos(childFile, basePath + file.getName() + File.separator, zos);
                }
            }
        } else {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basePath + file.getName());
            zos.putNextEntry(entry);
            int count = 0;
            byte data[] = new byte[1024];
            while ((count = bis.read(data)) != -1) {
                zos.write(data, 0, count);
            }
            bis.close();
        }
    }

    /**
     * 删除压缩包
     *
     * @param downloadFile 下载文件
     */
    public void deleteDownPackage(File downloadFile) {
        if (downloadFile.getName().endsWith("个文件.zip")) {
            downloadFile.delete();
        }
    }

    /**
     * 搜索文件
     *
     * @param request     请求
     * @param currentPath 当前路径
     * @param reg         关键词？
     * @param regType     文件类型
     * @return {@link List}<{@link FileCustom}>
     */
    public List<FileCustom> searchFile(HttpServletRequest request, String currentPath, String reg, String regType) {
        List<FileCustom> list = new ArrayList<>();
        matchFile(request, list, new File(getSearchFileName(request, currentPath)), reg, regType == null ? "" : regType);
        return list;
    }

    private String getSearchFileName(HttpServletRequest request, String fileName) {
        if (fileName == null || fileName.equals("\\")) {
            System.out.println(1);
            fileName = "";
        }
        String username = UserUtils.getUsername(request);
        String realpath = getRootPath(request) + username + File.separator + fileName;
        return realpath;
    }

    private void matchFile(HttpServletRequest request, List<FileCustom> list, File dirFile, String reg, String regType) {
        File[] listFiles = dirFile.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isFile()) {
                    String suffixType = FileUtils.getFileType(file);
                    //匹配后缀（分类）||匹配关键字（查询）
                    if (suffixType.equals(regType) || (reg != null && file.getName().contains(reg))) {
                        FileCustom custom = new FileCustom();
                        custom.setFileName(file.getName());
                        custom.setLastTime(FileUtils.formatTime(file.lastModified()));
                        String parentPath = file.getParent();
                        String prePath = parentPath.substring(
                                parentPath.indexOf(getSearchFileName(request, null)) + getSearchFileName(request, null).length());
                        custom.setCurrentPath(File.separator + prePath);
                        if (file.isDirectory()) {
                            custom.setFileSize("-");
                        } else {
                            custom.setFileSize(FileUtils.getDataSize(file.length()));
                        }
                        custom.setFileType(FileUtils.getFileType(file));
                        list.add(custom);
                    }
                } else {
                    matchFile(request, list, file, reg, regType);
                }
            }
        }
    }
    //编码转换
    public void respFile(HttpServletResponse response, HttpServletRequest request, String currentPath, String fileName, String type) throws IOException {
        File file = new File(getFileName(request, currentPath), fileName);
        InputStream inputStream = new FileInputStream(file);
        if ("docum".equals(type)) {
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(inputStream, response.getWriter(), "UTF-8");
        } else {
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }

    /**
     * 打开文档文件
     *
     * @param request
     * @param currentPath
     * @param fileName
     * @return
     * @throws Exception
     */
    public String openOffice(HttpServletRequest request, String currentPath, String fileName) throws Exception {
        return officeDao.getOfficeId(FileUtils.MD5(new File(getFileName(request, currentPath), fileName)));
    }

    /*--回收站显示所有删除文件--*/
    public List<RecycleFile> recycleFiles(HttpServletRequest request) throws Exception {
        List<RecycleFile> recycleFiles = fileDao.selectFiles(UserUtils.getUsername(request));
        for (RecycleFile file : recycleFiles) {
            File f = new File(getRecyclePath(request), new File(file.getFilePath()).getName());
            file.setFileName(f.getName());
            file.setLastTime(FileUtils.formatTime(f.lastModified()));
        }
        return recycleFiles;
    }
}
