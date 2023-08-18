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

package com.luckykuang.mongodb.service;

import com.luckykuang.mongodb.entity.PageResult;
import com.luckykuang.mongodb.entity.User;
import com.luckykuang.mongodb.vo.UserVO;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/8/18 10:26
 */
public interface MongodbService {

    User saveUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(String id);

    List<User> findUserById(String id);

    List<User> findUserByName(String name);

    PageResult<User> findUserByPage(Integer current, Integer size, String name);

    PageResult<UserVO> findUserVOByPage(Integer current, Integer size);
}
