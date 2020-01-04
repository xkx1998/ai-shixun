package com.shixun.aiweb.service;

import com.baidu.aip.ocr.AipOcr;

/**
 * @author chm
 * @date 2020/1/2 9:15
 */
public interface RecognizationService {

    /**
     * 开始识别
     * @return
     */
    public String recognize(String image);
}
