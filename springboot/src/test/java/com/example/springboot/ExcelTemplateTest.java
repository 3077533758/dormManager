package com.example.springboot;

import com.example.springboot.common.ExcelTemplateGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest
public class ExcelTemplateTest {

    @Test
    public void testGenerateTemplate() {
        try {
            // 生成模板字节数组
            byte[] templateBytes = ExcelTemplateGenerator.generateBlankTemplate();
            
            // 保存到文件进行验证
            try (FileOutputStream fos = new FileOutputStream("test_template.xlsx")) {
                fos.write(templateBytes);
            }
            
            System.out.println("Excel模板生成成功，文件大小: " + templateBytes.length + " 字节");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 