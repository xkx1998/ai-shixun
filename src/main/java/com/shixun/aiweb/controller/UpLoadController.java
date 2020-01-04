package com.shixun.aiweb.controller;

import com.baidu.aip.ocr.AipOcr;
import com.shixun.aiweb.service.BasicGeneralService;
import com.shixun.aiweb.service.IdCardService;
import com.shixun.aiweb.service.PlateLicenseService;
import com.shixun.aiweb.service.ReceiptService;
import com.shixun.aiweb.service.impl.BasicGeneralServiceImpl;
import com.shixun.aiweb.service.impl.IdCardServiceImpl;
import com.shixun.aiweb.service.impl.PlateLicenseServiceImpl;
import com.shixun.aiweb.service.impl.ReceiptServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Controller
public class UpLoadController {
    //设置APPID/AK/SK
    public static final String APP_ID = "11469861";
    public static final String API_KEY = "2NmBChTTNG33BVixnbiZiMLR";
    public static final String SECRET_KEY = "l2gjFmGcxgnUUw6WIBQIGUVQkEqqnGNM";

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String carFileUpload(RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile srcFile,
                                @RequestParam("type") String type) {
        String imgUrl = "";
        //选择了文件，开始进行上传操作
        try {
            String fileName = srcFile.getOriginalFilename();
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            fileName = UUID.randomUUID() + suffixName;
            //构建上传目标路径,
            File destFile = new File
                    (ResourceUtils.getURL("classpath:").getPath());
            if (!destFile.exists()) {
                destFile = new File("");
            }
            //出处目标文件的绝对路径
            System.out.println("file path" + destFile.getAbsolutePath());
            File upload = new File(destFile.getAbsolutePath() + "\\src\\main\\resources\\upload");


            //若目标文件夹不存在，则创建一个
            if (!upload.exists()) {
                upload.mkdir();
            }
            System.out.println("完整的上传路径:" + upload.getAbsolutePath() + "\\" + fileName);

            //根据srcFile的大小，准备一个字节数组
            byte[] bytes = srcFile.getBytes();
            //拼接上传路径
            Path path = Paths.get(upload.getAbsolutePath() + "\\" + fileName);
            //最重要的一步，将源文件写入目标地址
            Files.write(path, bytes);
            //将文件上传成功的信息写入message
            redirectAttributes.addFlashAttribute("message", "文件上传成功！" + fileName);
            imgUrl = upload.getAbsolutePath() + "\\" + fileName;
            // 初始化一个AipOcr
            AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

            // 设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);

            BasicGeneralService basicGeneralService = new BasicGeneralServiceImpl(client);
            if (type.equals("1")) {
                PlateLicenseService plateLicenseService = new PlateLicenseServiceImpl(client);
                System.out.println("车牌识别开始...");
                plateLicenseService.recognize(imgUrl);
            } else if (type.equals("2")) {
                IdCardService idCardService = new IdCardServiceImpl(client);
                System.out.println("身份证识别开始...");
                idCardService.recognize(imgUrl);
            } else if (type.equals("3")) {
                System.out.println("文字识别开始...");
                basicGeneralService.recognize(imgUrl);
            } else {
                ReceiptService receiptService = new ReceiptServiceImpl(client);
                System.out.println("发票识别开始...");
                receiptService.recognize(imgUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}
