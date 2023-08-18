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

package com.luckykuang.mongodb.service.impl;

import com.luckykuang.mongodb.entity.PageResult;
import com.luckykuang.mongodb.entity.User;
import com.luckykuang.mongodb.service.MongodbService;
import com.luckykuang.mongodb.util.MongoPageHelper;
import com.luckykuang.mongodb.vo.UserVO;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/8/18 10:26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MongodbServiceImpl implements MongodbService {

    private final MongoTemplate mongoTemplate;

    @Override
    public User saveUser(User user) {
        return mongoTemplate.save(user);
    }

    @Override
    public boolean updateUser(User user) {
        List<User> users = mongoTemplate.find(new Query(Criteria.where("_id").is(user.getId())), User.class);
        if (users.isEmpty()){
            throw new RuntimeException("用户不存在");
        }
        UpdateResult updateResult = mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(user.getId())),
                new Update()
                        .set("name",user.getName())
                        .set("age",user.getAge()),
                User.class);
        return updateResult.wasAcknowledged();
    }

    @Override
    public boolean deleteUser(String id) {
        DeleteResult deleteResult = mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), User.class);
        return deleteResult.wasAcknowledged();
    }

    @Override
    public List<User> findUserById(String id) {
        return mongoTemplate.find(new Query(Criteria.where("_id").is(id)),User.class);
    }

    @Override
    public List<User> findUserByName(String name) {
        return mongoTemplate.find(new Query(Criteria.where("name").is(name)),User.class);
    }

    @Override
    public PageResult<User> findUserByPage(Integer current, Integer size, String name) {
        Query query = new Query();
        if (StringUtils.isNotBlank(name)){
            query.addCriteria(Criteria.where("name").is(name));
        }
        return MongoPageHelper.pageQuery(query, User.class, current, size);
    }

    @Override
    public PageResult<UserVO> findUserVOByPage(Integer current, Integer size) {
        return MongoPageHelper.pageQuery(
                new Query(),
                User.class,
                user -> {
                    UserVO userVO = new UserVO();
                    userVO.setName(user.getName());
                    userVO.setAge(user.getAge());
                    return userVO;
                } ,
                current, size);
    }
}
