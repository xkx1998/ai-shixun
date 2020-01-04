package com.shixun.aiweb.service.impl;

import com.baidu.aip.ocr.AipOcr;
import com.baidu.aip.util.Util;
import com.shixun.aiweb.service.BasicGeneralService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author chm
 * 通用文字识别模块
 * @date 2020/1/2 9:12
 */
public class BasicGeneralServiceImpl implements BasicGeneralService {

    private AipOcr client;

    // 可选参数调用接口
    HashMap<String, String> options = new HashMap<String, String>();

    public BasicGeneralServiceImpl(AipOcr aipOcr){
        client = aipOcr;
    }

    /**
     * 开始识别
     * @return
     */
    @Override
    public String recognize(String image) {

        options.put("language_type", "CHN_ENG");

        options.put("detect_direction", "true");

        options.put("detect_language", "true");

        options.put("probability", "true");

        // 参数为本地图片路径
       // String image = "D:\\IDEA\\AI实训项目\\AIRecognition\\src\\main\\resources\\testPic\\words.jpg";
        JSONObject res = client.basicGeneral(image, options);
        System.out.println(res.toString(2));

        // 参数为本地图片二进制数组
        byte[] file = new byte[0];
        try {
            file = Util.readFileByBytes(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        res = client.basicGeneral(file, options);
        System.out.println(res.toString(2));

        return res.toString(2);
    }

    /**
     * 通用文字识别(高精度版)
     *
     * @return
     */
    @Override
    public String highPrecisionRecognization(String image) {

        /**
         * 是否检测图像朝向，默认不检测，即：false。朝向是指输入图像是正常方向、逆时针旋转90/180/270度。可选值包括:
         * - true：检测朝向；
         * - false：不检测朝向。
         */
        options.put("detect_direction", "true");

        /**
         * 是否返回识别结果中每一行的置信度
         */
        options.put("probability", "true");

        JSONObject res = client.basicAccurateGeneral(image, options);
        System.out.println(res.toString(2));

        // 参数为本地图片二进制数组
        byte[] file = new byte[0];
        try {
            file = Util.readFileByBytes(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        res = client.basicAccurateGeneral(file, options);
        System.out.println(res.toString(2));

        return res.toString(2);
    }

    /**
     * 生僻字识别
     * （免费版没有该功能）
     * @return
     */
    @Override
    public String uncommonWordsRecognization() {

        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");

        // 参数为本地图片路径
        String image = "D:\\IDEA\\AI实训项目\\AIRecognition\\src\\main\\resources\\testPic\\uncommonWords.jpg";
        JSONObject res = client.enhancedGeneral(image, options);

        // 参数为本地图片二进制数组
//        byte[] file = new byte[0];
//        try {
//            file = Util.readFileByBytes(image);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        res = client.enhancedGeneral(file, options);
//        System.out.println(res.toString(2));


        // 通用文字识别（含生僻字版）, 图片参数为远程url图片
//        res = client.enhancedGeneralUrl("", options);
//        System.out.println(res.toString(2));
        return res.toString(2);
    }
}
