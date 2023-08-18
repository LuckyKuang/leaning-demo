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

package com.luckykuang.mongodb.controller;

import com.luckykuang.mongodb.entity.PageResult;
import com.luckykuang.mongodb.entity.User;
import com.luckykuang.mongodb.service.MongodbService;
import com.luckykuang.mongodb.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/8/18 10:25
 */
@Tag(name = "mongodb测试")
@RestController
@RequestMapping("mongodb")
@RequiredArgsConstructor
public class MongodbController {

    private final MongodbService mongodbService;

    @Operation(summary = "新增用户")
    @PostMapping("save")
    public User saveUser(@RequestBody @Validated User user){
        return mongodbService.saveUser(user);
    }

    @Operation(summary = "修改用户")
    @PutMapping("update")
    public boolean updateUser(@RequestBody @Validated User user){
        return mongodbService.updateUser(user);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("delete/{id}")
    public boolean deleteUser(@PathVariable("id") String id){
        return mongodbService.deleteUser(id);
    }

    @Operation(summary = "通过id查询用户")
    @GetMapping("findById")
    public List<User> findUserById(String id){
        return mongodbService.findUserById(id);
    }

    @Operation(summary = "通过name查询用户")
    @GetMapping("findByName")
    public List<User> findUserByName(String name){
        return mongodbService.findUserByName(name);
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("findPage")
    public PageResult<User> findUserByPage(Integer current, Integer size,  @RequestParam(required = false) String name){
        return mongodbService.findUserByPage(current,size,name);
    }

    @Operation(summary = "分页查询用户(无id)")
    @GetMapping("findPageVO")
    public PageResult<UserVO> findUserByPage(Integer current, Integer size){
        return mongodbService.findUserVOByPage(current,size);
    }
}
