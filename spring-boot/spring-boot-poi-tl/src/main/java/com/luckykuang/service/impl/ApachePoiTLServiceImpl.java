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

package com.luckykuang.service.impl;

import com.deepoove.poi.data.NumbericRenderData;
import com.deepoove.poi.data.SeriesRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.luckykuang.enums.ChartsCombinationTypeEnum;
import com.luckykuang.enums.PictureTypeEnum;
import com.luckykuang.enums.WordContentTypeEnum;
import com.luckykuang.model.*;
import com.luckykuang.service.ApachePoiTLService;
import com.luckykuang.util.OperateWordManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author luckykuang
 * @date 2024/4/15 11:04
 */
@Slf4j
@Service
public class ApachePoiTLServiceImpl implements ApachePoiTLService {

    private static final String OUTPUT_MKDIR = "template/";
    private static final String OUTPUT_FILENAME_SUFFIX = ".docx";

    @Override
    public void generateCharts() {
        try {
            File templateFile = new ClassPathResource("template/demo_template_2.docx").getFile();
            List<LabelData> labelDataList = new ArrayList<>();

            // 文本
            TextSeriesRenderData contentData = new TextSeriesRenderData();
            contentData
                    .setContent("2024年月通报函生成报告")
                    .setLabelName("title")
                    .setTypeEnum(WordContentTypeEnum.TEXT);
            labelDataList.add(contentData);

            // 带样式文本
            TextRenderData textRenderData = new TextRenderData("cc0000", "这是带样式的内容");
            textRenderData.setStyle(new Style("cc0000"));
            textRenderData.setText("2024年月通报函生成报告带样式文本");
            TextSeriesRenderData typeData = new TextSeriesRenderData();
            typeData
                    .setRenderData(textRenderData)
                    .setLabelName("typeContent")
                    .setTypeEnum(WordContentTypeEnum.TEXT);
            labelDataList.add(typeData);

            // 插入图片
            PictureSeriesRenderData picData = new PictureSeriesRenderData();
            picData
                    .setWidth(200)
                    .setHeight(160)
                    .setPicType(PictureTypeEnum.JPG)
                    .setFile(new ClassPathResource("images/gaara.jpg").getFile())
                    .setLabelName("picture")
                    .setTypeEnum(WordContentTypeEnum.PICTURE);
            labelDataList.add(picData);

            // 插入表格
            TableSeriesRenderData tableData = new TableSeriesRenderData();
            // 表头
            TextRenderData[] header = new TextRenderData[]{new TextRenderData("班级"),new TextRenderData("排名")};
            // 表格内容
            List<TextRenderData[]> contents = Arrays.asList(new TextRenderData[]{new TextRenderData("科教1班"),
                    new TextRenderData("1")},new TextRenderData[]{new TextRenderData("幼儿3班"),new TextRenderData("6")});
            tableData
                    .setHeader(header)
                    .setContents(contents)
                    .setLabelName("showTable")
                    .setTypeEnum(WordContentTypeEnum.TABLE);
            labelDataList.add(tableData);

            // 插入列表
            ListSeriesRenderData listSeriesRenderData = new ListSeriesRenderData();
            List<TextRenderData> listData = Arrays.asList(new TextRenderData("排序1"),new TextRenderData("排序2"),new TextRenderData("排序3"));
            listSeriesRenderData
                    .setList(listData)
                    .setPair(NumbericRenderData.FMT_DECIMAL_PARENTHESES)
                    .setLabelName("numList")
                    .setTypeEnum(WordContentTypeEnum.LIST);
            labelDataList.add(listSeriesRenderData);

            // 折线图
            ChartsSeriesRenderData lineData = new ChartsSeriesRenderData();
            List<ChartsSeriesRenderDataItem> lineRenderData = new ArrayList<>();
            ChartsSeriesRenderDataItem numRenderData = new ChartsSeriesRenderDataItem();
            ChartsSeriesRenderDataItem moneyRenderData = new ChartsSeriesRenderDataItem();
            numRenderData.setRenderTitle("项目数量").setData(new Double[] {-11.02,-19.42,-10.61,-11.41,-7.91,-5.44,-5.30,-2.75,-1.24,0.35});
            moneyRenderData.setRenderTitle("投资额").setData(new Number[]{-12.66,-19.41,-15.16,-19.72,-17.05,-15.92,-15.10,-13.04,-10.65,-9.15});
            lineRenderData.add(numRenderData);
            lineRenderData.add(moneyRenderData);
            lineData.setTitle("1-10月份全国新开工项目数量、投资额增速")
                    .setCategories(new String[] {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月"})
                    .setSenderData(lineRenderData)
                    .setLabelName("speedLine")
                    .setTypeEnum(WordContentTypeEnum.CHARTS);
            labelDataList.add(lineData);

            // 柱状图
            ChartsSeriesRenderData barData = new ChartsSeriesRenderData();
            List<ChartsSeriesRenderDataItem> barRenderData = new ArrayList<>();
            ChartsSeriesRenderDataItem openRenderData = new ChartsSeriesRenderDataItem();
            ChartsSeriesRenderDataItem moneyData = new ChartsSeriesRenderDataItem();
            openRenderData.setRenderTitle("开工数量").setData(new Number[]{40,50,45,12,21,18,21,28,21,18,28,18,20,19,-10,-9,-10,19,39,31,20,19,-10,-9,-10,19,39,31,-10,19,39});
            moneyData.setRenderTitle("投资额").setData(new Number[]{20,-22,-12,8,-10,-14,-10,-10,-8,-2,-8,-1,-9,-21,-9,-7,-21,-10,21,-29,-50,-21,-9,-7,-21,-10,21,-29,-21,-10,21});
            barRenderData.add(openRenderData);
            barRenderData.add(moneyData);
            barData.setTitle("各省（自治区）直辖市新开项目数量、投资额同比情况")
                    .setCategories(new String[] {"贵州","西藏","黑龙江","浙江","湖北","江苏","四川","福建","安徽","海南","山西","广西","青海","广东","甘肃",
                            "云南","宁夏","新疆","湖南","北京","河北","山西","山东","内蒙古","天津","江西","吉林","河南","重庆","上海","辽宁"})
                    .setSenderData(barRenderData)
                    .setLabelName("investmentRatio")
                    .setTypeEnum(WordContentTypeEnum.CHARTS);
            labelDataList.add(barData);

            // 生成饼图
            ChartsSeriesRenderData areaData = new ChartsSeriesRenderData();
            List<ChartsSeriesRenderDataItem> areaRenderDatas = new ArrayList<>();
            ChartsSeriesRenderDataItem areaRenderData = new ChartsSeriesRenderDataItem();
            areaRenderData.setData(new Number[]{17098242, 9984670, 9826675, 9596961}).setRenderTitle("投资额")
                    .setComboType(SeriesRenderData.ComboType.AREA);
            areaRenderDatas.add(areaRenderData);
            areaData.setTitle("国家投资额").setSenderData(areaRenderDatas)
                    .setCharType(ChartsCombinationTypeEnum.SINGLE)
                    .setCategories(new String[]{"俄罗斯", "加拿大", "美国", "中国"})
                    .setLabelName("areaShow")
                    .setTypeEnum(WordContentTypeEnum.CHARTS);
            labelDataList.add(areaData);

            // 横向柱状图
            ChartsSeriesRenderData lateralData = new ChartsSeriesRenderData();
            List<ChartsSeriesRenderDataItem> lateralRenderData = new ArrayList<>();
            ChartsSeriesRenderDataItem lateralYearData = new ChartsSeriesRenderDataItem();
            ChartsSeriesRenderDataItem lateralMoneyData = new ChartsSeriesRenderDataItem();
            lateralYearData.setRenderTitle("2023年").setData(new Number[]{400,200});
            lateralMoneyData.setRenderTitle("2024年").setData(new Number[]{456,255});
            lateralRenderData.add(lateralYearData);
            lateralRenderData.add(lateralMoneyData);
            lateralData
                    .setTitle("工程建设项目建设周期同比情况")
                    .setCategories(new String[] {"从立项到开工的用时","从开工到验收的用时"})
                    .setSenderData(lateralRenderData)
                    .setLabelName("cycleRadio")
                    .setTypeEnum(WordContentTypeEnum.CHARTS);
            labelDataList.add(lateralData);

            // 组合图表
            ChartsSeriesRenderData groupData = new ChartsSeriesRenderData();
            List<ChartsSeriesRenderDataItem> groupRenderData = new ArrayList<>();
            ChartsSeriesRenderDataItem unOpenData = new ChartsSeriesRenderDataItem();
            ChartsSeriesRenderDataItem openRadioData = new ChartsSeriesRenderDataItem();
            unOpenData.setComboType(SeriesRenderData.ComboType.BAR).setRenderTitle("未开工项目数（个）")
                    .setData(new Number[]{55, 35, 23, 76, 60, 65.1, 70.2, 75.3, 80.4, 85.5, 90.6, 95.7, 26,
                            76, 60, 65.1, 70.2, 75.3, 80.4, 95.7, 26, 76, 60, 65.1, 70.2, 75.3, 95.7, 26, 76, 60, 65.1});
            openRadioData.setComboType(SeriesRenderData.ComboType.LINE).setRenderTitle("开工率（%）")
                    .setData(new Number[]{34,45,23,67,34,45,23,67,34,45,23,67,23,67,34,45,23,45,23,67,23,67,34,45,23,45,67,23,67,34,45});
            groupRenderData.add(unOpenData);
            groupRenderData.add(openRadioData);
            groupData
                    .setTitle("各省（区、市）签约项目开工情况")
                    .setCategories(new String[] {"北京","吉林","云南","上海","安徽","浙江","江西","四川","陕西","甘肃","江苏","广西","内蒙古","福建","天津","海南","黑龙江",
                            "贵州","山东","河北","辽宁","湖北","宁夏","广东","重庆","河南","新疆","山西","湖南","青海","兵团"})
                    .setSenderData(groupRenderData)
                    .setLabelName("openCondition")
                    .setTypeEnum(WordContentTypeEnum.CHARTS);
            labelDataList.add(groupData);

            File output = new File(OUTPUT_MKDIR);
            if (!output.exists()){
                output.mkdir();
            }
            String outputPath = this.getClass().getClassLoader().getResource(OUTPUT_MKDIR).getPath();

            //生成word
            OperateWordManage.generateWordContent(templateFile,outputPath + "test" +OUTPUT_FILENAME_SUFFIX,labelDataList);

        } catch (IOException e) {
            log.error("生成word错误",e);
        }
    }
}
