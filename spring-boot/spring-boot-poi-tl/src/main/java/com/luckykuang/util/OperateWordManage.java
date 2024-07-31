/*
 * Copyright 2015-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luckykuang.util;

import com.deepoove.poi.XWPFTemplate;
import com.luckykuang.factory.GenerateWord;
import com.luckykuang.factory.GenerateWordFactory;
import com.luckykuang.model.LabelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 操作word内容
 * @author luckykuang
 * @date 2024/4/15 11:05
 */
public class OperateWordManage {
    private final static Logger log = LoggerFactory.getLogger(OperateWordManage.class);

    public static void generateWordContent(File tempFileFile, String destFilePath, List<LabelData> contents){
        FileOutputStream fos = null;
        XWPFTemplate template = null;
        try {
            Map<String,Object> map = new HashMap<>(contents.size());
            contents.forEach(content ->{
                GenerateWord backData = GenerateWordFactory.getBackData(content.getTypeEnum());
                map.put(content.getLabelName(),backData.generateWord(content));
            });
            template = XWPFTemplate.compile(tempFileFile).render(map);
            fos = new FileOutputStream(destFilePath);
            template.write(fos);
            fos.flush();
        }catch (Exception e){
            log.error("替换生成图表报错：{}",e.getMessage());
        }finally {
            try{
                if (Objects.nonNull(fos)){
                    fos.close();
                }
                if (Objects.nonNull(template)){
                    template.close();
                }
            }catch (Exception e){
                log.error("关闭数据流报错：{}",e.getMessage());
            }
        }
    }
}
