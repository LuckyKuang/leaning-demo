package com.luckykuang.es.mapper;

import com.luckykuang.es.model.entity.EsUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author luckykuang
 * @since 2024/8/28 11:25
 */
@Slf4j
@SpringBootTest
class SearchTest {
    @Resource
    private EsUserMapper esUserMapper;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    void batchInsertTest() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        EsUser esUser1 = new EsUser();
        esUser1.setId(1);
        esUser1.setName("岳泽霖");
        esUser1.setNickName("小泽霖");
        esUser1.setAge(28);
        esUser1.setSex("男");
        Date birthday = sdf.parse("2024-08-20 00:10:10");
        esUser1.setBirthday(birthday);

        EsUser esUser2 = new EsUser();
        esUser2.setId(2);
        esUser2.setName("岳建立");
        esUser2.setNickName("小建立");
        esUser2.setAge(26);
        esUser2.setSex("男");
        Date birthday2 = sdf.parse("2024-08-20 00:10:20");
        esUser2.setBirthday(birthday2);

        EsUser esUser3 = new EsUser();
        esUser3.setId(3);
        esUser3.setName("张三");
        esUser3.setNickName("张三");
        esUser3.setAge(24);
        esUser3.setSex("男");
        Date birthday3 = sdf.parse("2024-08-20 00:10:30");
        esUser3.setBirthday(birthday3);

        EsUser esUser4 = new EsUser();
        esUser4.setId(4);
        esUser4.setName("李四");
        esUser4.setNickName("李四");
        esUser4.setAge(24);
        esUser4.setSex("女");
        Date birthday4 = sdf.parse("2024-08-20 00:10:40");
        esUser4.setBirthday(birthday4);

        EsUser esUser5 = new EsUser();
        esUser5.setId(5);
        esUser5.setName("王二");
        esUser5.setNickName("王二");
        esUser5.setAge(16);
        esUser5.setSex("女");
        Date birthday5 = sdf.parse("2024-08-20 00:10:50");
        esUser5.setBirthday(birthday5);

        EsUser esUser6 = new EsUser();
        esUser6.setId(6);
        esUser6.setName("赵六");
        esUser6.setNickName("赵六");
        esUser6.setAge(30);
        esUser6.setSex("男");
        Date birthday6 = sdf.parse("2024-08-20 01:00:00");
        esUser6.setBirthday(birthday6);

        EsUser esUser7 = new EsUser();
        esUser7.setId(7);
        esUser7.setName("孙七");
        esUser7.setNickName("孙七");
        esUser7.setAge(22);
        esUser7.setSex("女");
        Date birthday7 = sdf.parse("2024-08-20 02:00:00");
        esUser7.setBirthday(birthday7);

        EsUser esUser8 = new EsUser();
        esUser8.setId(8);
        esUser8.setName("周八");
        esUser8.setNickName("周八");
        esUser8.setAge(27);
        esUser8.setSex("男");
        Date birthday8 = sdf.parse("2024-08-20 03:00:00");
        esUser8.setBirthday(birthday8);

        EsUser esUser9 = new EsUser();
        esUser9.setId(9);
        esUser9.setName("吴九");
        esUser9.setNickName("吴九");
        esUser9.setAge(19);
        esUser9.setSex("女");
        Date birthday9 = sdf.parse("2024-08-20 04:00:00");
        esUser9.setBirthday(birthday9);

        EsUser esUser10 = new EsUser();
        esUser10.setId(10);
        esUser10.setName("郑十");
        esUser10.setNickName("郑十");
        esUser10.setAge(25);
        esUser10.setSex("男");
        Date birthday10 = sdf.parse("2024-08-20 05:00:00");
        esUser10.setBirthday(birthday10);

        EsUser esUser11 = new EsUser();
        esUser11.setId(11);
        esUser11.setName("冯十一");
        esUser11.setNickName("冯十一");
        esUser11.setAge(35);
        esUser11.setSex("女");
        Date birthday11 = sdf.parse("2024-08-20 06:00:00");
        esUser11.setBirthday(birthday11);

        EsUser esUser12 = new EsUser();
        esUser12.setId(12);
        esUser12.setName("陈十二");
        esUser12.setNickName("陈十二");
        esUser12.setAge(28);
        esUser12.setSex("男");
        Date birthday12 = sdf.parse("2024-08-20 07:00:00");
        esUser12.setBirthday(birthday12);

        EsUser esUser13 = new EsUser();
        esUser13.setId(13);
        esUser13.setName("魏十三");
        esUser13.setNickName("魏十三");
        esUser13.setAge(31);
        esUser13.setSex("女");
        Date birthday13 = sdf.parse("2024-08-20 08:00:00");
        esUser13.setBirthday(birthday13);

        EsUser esUser14 = new EsUser();
        esUser14.setId(14);
        esUser14.setName("蒋十四");
        esUser14.setNickName("蒋十四");
        esUser14.setAge(26);
        esUser14.setSex("男");
        Date birthday14 = sdf.parse("2024-08-20 09:00:00");
        esUser14.setBirthday(birthday14);

        EsUser esUser15 = new EsUser();
        esUser15.setId(15);
        esUser15.setName("沈十五");
        esUser15.setNickName("沈十五");
        esUser15.setAge(33);
        esUser15.setSex("女");
        Date birthday15 = sdf.parse("2024-08-20 10:00:00");
        esUser15.setBirthday(birthday15);

        List<EsUser> userList = new ArrayList<>();
        userList.add(esUser1);
        userList.add(esUser2);
        userList.add(esUser3);
        userList.add(esUser4);
        userList.add(esUser5);
        userList.add(esUser6);
        userList.add(esUser7);
        userList.add(esUser8);
        userList.add(esUser9);
        userList.add(esUser10);
        userList.add(esUser11);
        userList.add(esUser12);
        userList.add(esUser13);
        userList.add(esUser14);
        userList.add(esUser15);
        esUserMapper.insertBatch(userList);
    }

    @Test
    void equalsTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        //  String name = "岳泽霖";
        String name = "霖";
        lambdaEsQueryWrapper.eq(StringUtils.hasText(name), EsUser::getName,name);
        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }

    @Test
    void andTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        String name = "霖";
        String sex = "男";
        lambdaEsQueryWrapper.eq(StringUtils.hasText(name), EsUser::getName,name);
        lambdaEsQueryWrapper.eq(StringUtils.hasText(sex), EsUser::getSex,sex);
        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }

    @Test
    void orTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        String name = "霖";
        String sex = "男";
        lambdaEsQueryWrapper.eq(StringUtils.hasText(name), EsUser::getName,name);
        lambdaEsQueryWrapper.or().eq(StringUtils.hasText(sex), EsUser::getSex,sex);
        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }

    /**
     原先查询
     */
    @Test
    void originTest() throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        // 设置索引
        searchRequest.indices("es");

        /**
         构建条件
         */
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        // 进行请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits result = searchResponse.getHits();
        log.info(">>> 花费的时间:{}", searchResponse.getTook());
        log.info(">>>是否超时:{}", searchResponse.isTimedOut());
        log.info(">>>> 总的数量:{}", result.getTotalHits());
        log.info(">>>>最大的匹配分数值:{}", result.getMaxScore());
        log.info(">>>>查询结果输出开始");
        Arrays.stream(result.getHits()).forEach(
                n -> log.info(">>>获取内容:{}", n.getSourceAsString())
        );
        log.info(">>>> 查询结果输出结束");
    }
    /**
     分页查询
     */
    @Test
    void pageTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        EsPageInfo<EsUser> esUserPageInfo = esUserMapper.pageQuery(lambdaEsQueryWrapper, 1, 2);
        log.info(">>> 总数是: {}" ,esUserPageInfo.getTotal());
        printInfo(esUserPageInfo.getList());
    }

    /**
     排序查询
     */
    @Test
    void orderTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        // 进行排序
        lambdaEsQueryWrapper.orderByDesc(EsUser::getId);
        EsPageInfo<EsUser> esUserPageInfo = esUserMapper.pageQuery(lambdaEsQueryWrapper, 1, 5);
        log.info(">>> 总数是: {}" ,esUserPageInfo.getTotal());
        printInfo(esUserPageInfo.getList());
    }

    /**
     获取 Dsl 数据
     */
    @Test
    void getDslTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        // 进行排序
        lambdaEsQueryWrapper.orderByDesc(EsUser::getId);
        // from size
        lambdaEsQueryWrapper.limit((1-1) * 5,5);

        String source = esUserMapper.getSource(lambdaEsQueryWrapper);

        log.info(">>> 执行语句: {}", source);
    }
    /**
     只查询字段
     */
    @Test
    void selectTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        String name = "霖";
        String sex = "男";
        lambdaEsQueryWrapper.eq(StringUtils.hasText(name), EsUser::getName,name);
        lambdaEsQueryWrapper.eq(StringUtils.hasText(sex), EsUser::getSex,sex);

        lambdaEsQueryWrapper.select(EsUser::getId,EsUser::getName);

        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }

    /**
     单字段去重
     */
    @Test
    void dictTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        lambdaEsQueryWrapper.distinct(EsUser::getAge);
        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }

    /**
     不查询字段
     */
    @Test
    void notSelectTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        String name = "霖";
        String sex = "男";
        lambdaEsQueryWrapper.eq(StringUtils.hasText(name), EsUser::getName,name);
        lambdaEsQueryWrapper.eq(StringUtils.hasText(sex), EsUser::getSex,sex);

        lambdaEsQueryWrapper.notSelect(EsUser::getId,EsUser::getAge);

        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }


    /**
     匹配
     */
    @Test
    void matchTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        lambdaEsQueryWrapper.match(EsUser::getName,"霖",2.0f);
        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }

    /**
     匹配
     */
    @Test
    void queryTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        // lambdaEsQueryWrapper.queryStringQuery("霖",2.0f);
        lambdaEsQueryWrapper.prefixQuery(EsUser::getName,"岳",2.0f);
        List<EsUser> esUserList = esUserMapper.selectList(lambdaEsQueryWrapper);
        printInfo(esUserList);
    }

    /**
     聚合查询

     {"took":18,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},
     "aggregations":{"lterms#ageTerms":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0,
     "buckets":[{"key":24,"doc_count":2},{"key":16,"doc_count":1},{"key":26,"doc_count":1},
     {"key":28,"doc_count":1}]}}}
     */
    @Test
    void groupTest() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        lambdaEsQueryWrapper.groupBy(EsUser::getAge);
        SearchResponse searchResponse = esUserMapper.search(lambdaEsQueryWrapper);
        log.info(">>> 查询数据: {}" ,searchResponse);
    }

    @Test
    void group2Test() {
        LambdaEsQueryWrapper<EsUser> lambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        lambdaEsQueryWrapper.groupBy(EsUser::getAge);
        lambdaEsQueryWrapper.max(EsUser::getMaxAge);
        lambdaEsQueryWrapper.min(EsUser::getMinAge);
        SearchResponse searchResponse = esUserMapper.search(lambdaEsQueryWrapper);
        log.info(">>> 查询数据: {}" ,searchResponse);
    }


    void printInfo( List<EsUser> esUserList) {
        if (CollectionUtils.isEmpty(esUserList)){
            log.info(">>>> 未查询出用户信息");
            return ;
        }
        esUserList.forEach(n-> log.info("用户信息: {}" ,n));
    }
}
