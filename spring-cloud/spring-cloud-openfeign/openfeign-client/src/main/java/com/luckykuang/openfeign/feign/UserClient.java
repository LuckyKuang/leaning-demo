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

package com.luckykuang.openfeign.feign;

import com.luckykuang.common.model.User;
import com.luckykuang.openfeign.feign.factroy.UserClientFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/6/13 18:58
 */
@FeignClient(
        // 需要调用服务端的服务名
        name = "openfeign-server",
        // 路径前缀设置
        path = "api/v1/user",
        // 熔断处理
        fallbackFactory = UserClientFactory.class)
public interface UserClient {

    @PostMapping("save")
    User save(@RequestBody User user);

    @PostMapping(value = "saveByForm",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    User saveByForm(User user);

    @PostMapping("saveByParam")
    User saveByParam(@RequestParam("name") String name,
                     @RequestParam("age") Integer age);

    @PutMapping("update")
    User update(@RequestParam("id") Long id,
                @RequestParam("name") String name,
                @RequestParam("age") Integer age);

    @PutMapping("update/{id}")
    User update(@PathVariable("id") Long id, @RequestBody User user);


    @PutMapping(value = "updateByForm",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    User updateByForm(User user);

    @PutMapping("update/{id}")
    User updateByParam(@PathVariable("id") Long id, User user);

    @DeleteMapping("delete/{id}")
    Boolean delete(@PathVariable("id") Long id);

    @GetMapping("getUser/{id}")
    User getUser(@PathVariable("id") Long id);

    @GetMapping("getUsers")
    List<User> getUsers();

    @GetMapping("getUsersByPage")
    List<User> getUsersByPage(@RequestParam("current") Integer current,
                              @RequestParam("size") Integer size,
                              @RequestParam("id") Long id,
                              @RequestParam("name") String name,
                              @RequestParam("age") Integer age);

    @PostMapping(value = "uploadFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Boolean uploadFile(@RequestPart("file") MultipartFile file);
}
