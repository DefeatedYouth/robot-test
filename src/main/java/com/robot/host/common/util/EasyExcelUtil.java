package com.robot.host.common.util;

import com.alibaba.excel.EasyExcelFactory;

import java.io.InputStream;
import java.util.List;

public class EasyExcelUtil {


    /**
     * 同步按模型读(指定sheet和表头占的行数）
     * @param inputStream
     * @param clazz 模型的类类型
     * @param sheetNo   sheet页号,从0开始
     * @param headRowNum    表头占的行数,从0开始
     * @return
     */
    public static <T>List<T> syncReadModel(InputStream inputStream, Class clazz, Integer sheetNo, Integer headRowNum){
        return EasyExcelFactory.read(inputStream).sheet(sheetNo).headRowNumber(headRowNum).head(clazz).doReadSync();
    }
}
