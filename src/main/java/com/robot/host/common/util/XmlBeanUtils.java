package com.robot.host.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @program: com.face.ele.smart.modules.netty.utils
 * @description:
 * @author: dadi
 * @create: 2020-05-05 13:03
 */
@Slf4j
public class XmlBeanUtils {
    public static <T>T xmlToBean(String xmlStr, Class<T> load) {
        try {
            JAXBContext context = JAXBContext.newInstance(load);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            T object = null;
            object =(T) unmarshaller.unmarshal(sr);
            return object;
        } catch (JAXBException e) {
            log.error("xml转对象异常{}", e.fillInStackTrace());
            return null;
        }

    }

    public static String beanToXml(Object obj, Class<?> load) {
        try {
            JAXBContext context = JAXBContext.newInstance(load);
            Marshaller marshaller = null;
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            log.error("对象转xml异常{}", e.getMessage());
            return null;
        }
    }

   /* public static void main(String[] args) {
        RegisterXmlDTO registerDTO = new RegisterXmlDTO();
        registerDTO.setCode("1");
        try {
            System.out.println(beanToXml(registerDTO, RegisterXmlDTO.class));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }*/
}
