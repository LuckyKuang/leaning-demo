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

package com.luckykuang.openfeign.service;

import com.luckykuang.common.model.User;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/6/14 14:18
 */
public interface UserService {
    User save(User user);

    User saveByForm(User user);

    User saveByParam(String name, Integer age);

    User update(Long id, String name, Integer age);

    User update(Long id, User user);

    User updateByForm(User user);

    User updateByParam(Long id, User user);

    Boolean delete(Long id);

    User getUser(Long id);

    List<User> getUsers();
}
