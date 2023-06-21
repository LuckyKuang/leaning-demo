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

package com.luckykuang.openfeign.service.impl;

import com.luckykuang.common.model.User;
import com.luckykuang.openfeign.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luckykuang
 * @date 2023/6/14 14:18
 */
@Service
public class UserServiceImpl implements UserService {

    // 模拟数据库
    private static final Map<Long, User> cacheMap = new HashMap<>();

    @Override
    public User save(User user){
        Long id = getMaxId();
        user.setId(id);
        cacheMap.put(id,user);
        return cacheMap.get(id);
    }

    @Override
    public User saveByForm(User user){
        Long id = getMaxId();
        user.setId(id);
        cacheMap.put(id,user);
        return cacheMap.get(id);
    }

    @Override
    public User saveByParam(String name, Integer age){
        Long id = getMaxId();
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAge(age);
        cacheMap.put(id,user);
        return cacheMap.get(id);
    }

    @Override
    public User update(Long id, String name, Integer age){
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAge(age);
        cacheMap.put(id,user);
        return cacheMap.get(id);
    }

    @Override
    public User update(Long id, User userVo){
        User user = new User();
        user.setId(id);
        user.setName(userVo.getName());
        user.setAge(userVo.getAge());
        cacheMap.put(id,user);
        return cacheMap.get(id);
    }

    @Override
    public User updateByForm(User userVo){
        User user = new User();
        user.setId(userVo.getId());
        user.setName(userVo.getName());
        user.setAge(userVo.getAge());
        cacheMap.put(userVo.getId(),user);
        return cacheMap.get(userVo.getId());
    }

    @Override
    public User updateByParam(Long id, User userVo){
        User user = new User();
        user.setId(id);
        user.setName(userVo.getName());
        user.setAge(userVo.getAge());
        cacheMap.put(id,user);
        return cacheMap.get(id);
    }

    @Override
    public Boolean delete(Long id){
        cacheMap.remove(id);
        return true;
    }

    @Override
    public User getUser(Long id){
        return cacheMap.get(id);
    }

    @Override
    public List<User> getUsers(){
        List<User> users = new ArrayList<>();
        cacheMap.forEach((key,value) -> users.add(value));
        return users;
    }

    private Long getMaxId(){
        return cacheMap.isEmpty() ? 1L : cacheMap.size() + 1;
    }
}
