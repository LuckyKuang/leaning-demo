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

package com.luckykuang.openfeign.controller;

import com.luckykuang.common.model.User;
import com.luckykuang.openfeign.feign.UserClient;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/6/13 18:56
 */
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Resource
    private UserClient userClient;

    @PostMapping("save")
    public User save(@RequestBody User user){
        return userClient.save(user);
    }

    @PostMapping("saveByForm")
    public User saveByForm(User user){
        return userClient.saveByForm(user);
    }

    @PostMapping("saveByParam")
    public User saveByParam(String name, Integer age){
        return userClient.saveByParam(name,age);
    }

    @PutMapping("update")
    public User update(Long id, String name, Integer age){
        return userClient.update(id,name,age);
    }

    @PutMapping("update/{id}")
    public User update(@PathVariable("id") Long id, @RequestBody User user){
        return userClient.update(id,user);
    }

    @PutMapping("updateByForm")
    public User updateByForm(User user){
        return userClient.updateByForm(user);
    }

    @PutMapping("updateByParam/{id}")
    public User updateByParam(@PathVariable("id") Long id, User user){
        return userClient.updateByParam(id,user);
    }

    @DeleteMapping("delete/{id}")
    public Boolean delete(@PathVariable("id") Long id){
        return userClient.delete(id);
    }

    @GetMapping("getUser/{id}")
    public User getUser(@PathVariable("id") Long id){
        return userClient.getUser(id);
    }

    @GetMapping("getUsers")
    public List<User> getUsers(){
        return userClient.getUsers();
    }

    @GetMapping("getUsersByPage")
    public List<User> getUsersByPage(Integer current, Integer size, User user){
        return userClient.getUsersByPage(current, size, user.getId(),user.getName(),user.getAge());
    }

    @PostMapping("uploadFile")
    public Boolean uploadFile(@RequestPart("file") MultipartFile file){
        return userClient.uploadFile(file);
    }
}
