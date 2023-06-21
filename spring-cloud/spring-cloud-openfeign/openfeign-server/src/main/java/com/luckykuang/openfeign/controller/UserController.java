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
import com.luckykuang.openfeign.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务端
 * @author luckykuang
 * @date 2023/6/13 15:16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping("save")
    public User save(@RequestBody User user){
        return userService.save(user);
    }

    @PostMapping("saveByForm")
    public User saveByForm(User user){
        return userService.saveByForm(user);
    }

    @PostMapping("saveByParam")
    public User saveByParam(String name, Integer age){
        return userService.saveByParam(name,age);
    }

    @PutMapping("update")
    public User update(Long id, String name, Integer age){
        return userService.update(id,name,age);
    }

    @PutMapping("update/{id}")
    public User update(@PathVariable("id") Long id, @RequestBody User user){
        return userService.update(id,user);
    }

    @PutMapping("updateByForm")
    public User updateByForm(User user){
        return userService.updateByForm(user);
    }

    @PutMapping("updateByParam/{id}")
    public User updateByParam(@PathVariable("id") Long id, User user){
        return userService.updateByParam(id,user);
    }

    @DeleteMapping("delete/{id}")
    public Boolean delete(@PathVariable("id") Long id){
        return userService.delete(id);
    }

    @GetMapping("getUser/{id}")
    public User getUser(@PathVariable("id") Long id){
        return userService.getUser(id);
    }

    @GetMapping("getUsers")
    public List<User> getUsers(){
        return userService.getUsers();
    }
}
