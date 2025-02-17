package com.luckykuang.es.mapper;

import com.luckykuang.es.entity.EsUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 基础测试
 * @author luckykuang
 * @since 2024/8/28 10:54
 */
@Slf4j
@SpringBootTest
class BaseEsTest {

    @Resource
    private EsUserMapper esUserMapper;

    /**
     * 单个插入
     */
    @Test
    void insertTest() throws ParseException {
        EsUser esUser = new EsUser();
        esUser.setId(1);
        esUser.setName("岳泽霖");
        esUser.setNickName("小泽霖");
        esUser.setAge(28);
        esUser.setSex("男");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birthday = sdf.parse("2024-08-20 00:23:50");
        esUser.setBirthday(birthday);
        esUserMapper.insert(esUser);
    }

    /**
     * 批量插入
     */
    @Test
    void batchInsertTest() throws ParseException {
        EsUser esUser = new EsUser();
        esUser.setId(2);
        esUser.setName("岳建立");
        esUser.setNickName("小建立");
        esUser.setAge(25);
        esUser.setSex("男");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birthday = sdf.parse("2024-08-20 11:11:11");
        esUser.setBirthday(birthday);
        // 批量插入
        esUserMapper.insertBatch(Collections.singletonList(esUser));
    }

    /**
     * 查询所有
     */
    @Test
    void selectAll2Test() throws ParseException {
        LambdaEsQueryWrapper<EsUser> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(EsUser::getBirthday, "2024-08-20 00:10:33");
        List<EsUser> esUserList = esUserMapper.selectList(wrapper);
        esUserList.forEach(n-> log.info("用户信息: {}",n));
    }

    /**
     * 通过id查询
     */
    @Test
    void getByIdTest() {
        EsUser esUser = esUserMapper.selectById(1);
        log.info(">> 查询用户: {}",esUser );
    }

    /**
     * 查询所有
     */
    @Test
    void selectAllTest() {
        List<EsUser> esUserList = esUserMapper.selectList(new LambdaEsQueryWrapper<>());
        esUserList.forEach(n-> log.info("用户信息: {}",n));
    }

    /**
     * 根据id集合 批量查询
     */
    @Test
    void getByIdsTest() {
        List<EsUser> esUserList = esUserMapper.selectBatchIds(Arrays.asList(1,2));
        esUserList.forEach(n-> log.info("用户信息: {}",n));
    }

    /**
     * 查询总数量
     */
    @Test
    void countTest() {
        Long count = esUserMapper.selectCount(new LambdaEsQueryWrapper<>());
        log.info(">>> 总数是: {}", count);
    }

    /**
     * 更新操作
     */
    @Test
    void updateTest() {

        log.info(">>> 之前的数据是: {}" ,esUserMapper.selectById(1));

        EsUser esUser = esUserMapper.selectById(1);
        esUser.setAge(29);
        esUser.setNickName("两个蝴蝶飞");
        // 进行更新
        esUserMapper.updateById(esUser);

        log.info(">>> 修改后的数据是: {}" ,esUserMapper.selectById(1));
    }

    /**
     * 批量更新
     */
    @Test
    void batchUpdateTest() {
        EsUser esUser = esUserMapper.selectById(1);
        esUser.setAge(30);
        esUser.setNickName("批量更新两个蝴蝶飞");

        esUserMapper.updateBatchByIds(Collections.singletonList(esUser));

        log.info(">>> 修改后的数据是: {}" ,esUserMapper.selectById(1));

    }

    /**
     * 根据条件进行更新
     */
    @Test
    void updateByWrapperTest() {
        LambdaEsUpdateWrapper<EsUser> esUserLambdaEsUpdateWrapper = new LambdaEsUpdateWrapper<>();
        esUserLambdaEsUpdateWrapper.le(EsUser::getAge,300);
        EsUser esUser = new EsUser();
        esUser.setNickName("根据条件更新2");
        esUser.setAge(33);
        esUserMapper.update(esUser,esUserLambdaEsUpdateWrapper);
        selectAllTest();
    }

    /**
     * 根据id 进行删除
     */
    @Test
    void deleteByIdTest() {
        esUserMapper.deleteById(1);
    }

    /**
     * 根据id集合 批量删除
     */
    @Test
    void deleteBatchTest() {
        esUserMapper.deleteBatchIds(Arrays.asList(1,2,3,4,5));
    }

    /**
     根据条件批量删除
     */
    @Test
    void deleteByWrapperTest() throws Exception{
        LambdaEsQueryWrapper<EsUser> esUserLambdaEsQueryWrapper = new LambdaEsQueryWrapper<>();
        esUserLambdaEsQueryWrapper.le(EsUser::getAge,300);
        esUserMapper.delete(esUserLambdaEsQueryWrapper);
        TimeUnit.SECONDS.sleep(2);
        selectAllTest();
    }
}