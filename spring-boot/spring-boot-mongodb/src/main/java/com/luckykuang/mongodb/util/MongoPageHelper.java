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

package com.luckykuang.mongodb.util;

import com.luckykuang.mongodb.entity.PageResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.function.Function;

/**
 * MongoDB分页查询工具类
 * @author luckykuang
 * @date 2023/8/18 15:46
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MongoPageHelper {

    /**
     * 起始页号
     */
    private static final int FIRST_PAGE_NUM = 1;
    /**
     * mongodb中的id字段
     */
    private static final String ID = "_id";

    /**
     * 分页查询，直接返回集合类型的结果
     */
    public static <T> PageResult<T> pageQuery(Query query, Class<T> entityClass, Integer pageNum, Integer pageSize) {
        return pageQuery(query, entityClass, Function.identity(), pageNum, pageSize, null);
    }

    /**
     * 分页查询，不考虑条件分页，直接使用skip-limit来分页
     */
    public static <T, R> PageResult<R> pageQuery(Query query, Class<T> entityClass, Function<T, R> mapper,
                                          Integer pageNum, Integer pageSize) {
        return pageQuery(query, entityClass, mapper, pageNum, pageSize, null);
    }

    /**
     * 分页查询 采用find(_id>lastId).limit分页
     */
    public static <T> PageResult<T> pageQuery(Query query, Class<T> entityClass, Integer pageNum, Integer pageSize, String lastId) {
        return pageQuery(query, entityClass, Function.identity(), pageNum, pageSize, lastId);
    }

    /**
     * 分页查询
     *
     * @param query Mongo Query对象，构造你自己的查询条件.
     * @param entityClass Mongo collection定义的entity class，用来确定查询哪个集合.
     * @param mapper 映射器，你从db查出来的list的元素类型是entityClass, 如果你想要转换成另一个对象，比如去掉敏感字段等，可以使用mapper来决定如何转换.
     * @param pageNum 当前页.
     * @param pageSize 分页的大小.
     * @param lastId 条件分页参数, 区别于skip-limit，采用find(_id>lastId).limit分页.
     * 如果不跳页，像朋友圈，微博这样下拉刷新的分页需求，需要传递上一页的最后一条记录的ObjectId。 如果是null，则返回pageNum那一页.
     * @param <T> collection定义的class类型.
     * @param <R> 最终返回时，展现给页面时的一条记录的类型。
     * @return PageResult，一个封装page信息的对象.
     *
     * @see MongoPageHelper#pageQuery(org.springframework.data.mongodb.core.query.Query,java.lang.Class,
     * java.util.function.Function,java.lang.Integer,java.lang.Integer,java.lang.String)
     *
     */
    public static <T, R> PageResult<R> pageQuery(Query query, Class<T> entityClass, Function<T, R> mapper,
                                          Integer pageNum, Integer pageSize, String lastId) {
        MongoTemplate mongoTemplate = ApplicationContextUtils.getBean(MongoTemplate.class);
        //分页逻辑
        long total = mongoTemplate.count(query, entityClass);
        final Integer pages = (int) Math.ceil(total / (double) pageSize);
        if (pageNum <= 0 || pageNum > pages) {
            pageNum = FIRST_PAGE_NUM;
        }
        final Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(lastId)) {
            if (pageNum != FIRST_PAGE_NUM) {
                criteria.and(ID).gt(new ObjectId(lastId));
            }
            query.limit(pageSize);
        } else {
            int skip = pageSize * (pageNum - 1);
            query.skip(skip).limit(pageSize);
        }

        final List<T> entityList = mongoTemplate
                .find(query.addCriteria(criteria)
                                .with(Sort.by(Order.asc(ID))),
                        entityClass);

        final PageResult<R> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPages(pages);
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(pageNum);
        pageResult.setList(entityList.stream().map(mapper).toList());
        return pageResult;
    }
}
