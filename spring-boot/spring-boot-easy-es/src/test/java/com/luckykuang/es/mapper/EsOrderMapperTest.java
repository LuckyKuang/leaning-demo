package com.luckykuang.es.mapper;

import com.luckykuang.es.model.entity.EsOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author luckykuang
 * @since 2024/9/21 11:56
 */
@Slf4j
@SpringBootTest
class EsOrderMapperTest {

    @Resource
    private EsOrderMapper esOrderMapper;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void insert() throws ParseException {
        EsOrder esOrder = new EsOrder();
        esOrder.setId(1L);
        esOrder.setOrderName("test1");
        esOrder.setOrderPrice(new BigDecimal("2222.49"));
        esOrder.setOrderStatus(4);
        Date date = sdf.parse("2024-09-21 13:22:47");
        long time = date.getTime();
        esOrder.setOrderTime(time);
        esOrderMapper.insert(esOrder);

        EsOrder esOrder2 = new EsOrder();
        esOrder2.setId(2L);
        esOrder2.setOrderName("test2");
        esOrder2.setOrderPrice(new BigDecimal("3333.70"));
        esOrder2.setOrderStatus(5);
        Date date2 = sdf.parse("2024-09-21 13:33:47");
        long time2 = date2.getTime();
        esOrder2.setOrderTime(time2);
        esOrderMapper.insert(esOrder2);

        EsOrder esOrder3 = new EsOrder();
        esOrder3.setId(3L);
        esOrder3.setOrderName("test3");
        esOrder3.setOrderPrice(new BigDecimal("4444.87"));
        esOrder3.setOrderStatus(7);
        Date date3 = sdf.parse("2024-09-21 13:44:47");
        long time3 = date3.getTime();
        esOrder3.setOrderTime(time3);
        esOrderMapper.insert(esOrder3);
    }

    @Test
    void select1() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("2024-09-21 13:33:47");
        long time = date.getTime();
        System.out.println("时间："+time);
        List<EsOrder> esOrders = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().lt(EsOrder::getOrderTime, time));
        Assertions.assertEquals(1, esOrders.size());
        List<EsOrder> esOrders2 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().le(EsOrder::getOrderTime, time));
        Assertions.assertEquals(2, esOrders2.size());
        List<EsOrder> esOrders3 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().eq(EsOrder::getOrderTime, time));
        Assertions.assertEquals(1, esOrders3.size());

        BigDecimal a = new BigDecimal("3333.70");
        List<EsOrder> esOrders4 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().lt(EsOrder::getOrderPrice, a));
        Assertions.assertEquals(1, esOrders4.size());
        List<EsOrder> esOrders5 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().le(EsOrder::getOrderPrice, a));
        Assertions.assertEquals(2, esOrders5.size());
        List<EsOrder> esOrders6 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().eq(EsOrder::getOrderPrice, a));
        Assertions.assertEquals(1, esOrders6.size());

        String orderName1 = "test1";
        List<EsOrder> esOrders7 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().eq(EsOrder::getOrderName, orderName1));
        Assertions.assertEquals(1, esOrders7.size());
        String orderName2 = "test";
        List<EsOrder> esOrders8 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().likeRight(EsOrder::getOrderName, orderName2));
        Assertions.assertEquals(3, esOrders8.size());
        List<EsOrder> esOrders9 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().likeLeft(EsOrder::getOrderName, orderName2));
        Assertions.assertEquals(0, esOrders9.size());
        List<EsOrder> esOrders10 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().like(EsOrder::getOrderName, orderName2));
        Assertions.assertEquals(3, esOrders10.size());

        int b = 5;
        List<EsOrder> esOrders11 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().lt(EsOrder::getOrderStatus, b));
        Assertions.assertEquals(1, esOrders11.size());
        List<EsOrder> esOrders12 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().le(EsOrder::getOrderStatus, b));
        Assertions.assertEquals(2, esOrders12.size());
        List<EsOrder> esOrders13 = esOrderMapper.selectList(new LambdaEsQueryWrapper<EsOrder>().eq(EsOrder::getOrderStatus, b));
        Assertions.assertEquals(1, esOrders13.size());

        EsOrder esOrder = esOrderMapper.selectById(1L);
        Assertions.assertEquals("test1", esOrder.getOrderName());
    }

    @Test
    void delete() {
        esOrderMapper.deleteIndex("es_order");
    }
}