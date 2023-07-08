package com.qst.yunpan.controller;

import com.qst.yunpan.pojo.Result;
import com.qst.yunpan.pojo.ShareFile;
import com.qst.yunpan.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ShareController {
    @Autowired
    private ShareService shareService;

    /**
     * 分享文件
     *
     * @param request
     * @param currentPath 当前路径
     * @param shareFile   分享文件名
     * @return
     */
    @RequestMapping("/shareFile")
    public @ResponseBody Result<String> shareFile(HttpServletRequest request, String currentPath, String[] shareFile) {
        try {
            String shareUrl = shareService.shareFile(request, currentPath, shareFile);
            Result<String> result = new Result<>(405, true, "分享成功");
            result.setData(shareUrl);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(401, false, "分享失败");
        }
    }

    /**
     * 分享外链访问
     *
     * @param request  请求
     * @param shareUrl 分享网址
     * @return {@link String}
     */
    @RequestMapping("/share")
    public String share(HttpServletRequest request, String shareUrl) {
        try {
            List<ShareFile> files = shareService.findShare(request, shareUrl);
            request.setAttribute("files", files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "share";
    }
}