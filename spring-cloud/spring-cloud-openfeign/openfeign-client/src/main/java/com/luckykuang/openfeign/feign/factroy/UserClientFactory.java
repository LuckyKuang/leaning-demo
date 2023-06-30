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

package com.luckykuang.openfeign.feign.factroy;

import com.luckykuang.common.exception.BusinessException;
import com.luckykuang.common.model.User;
import com.luckykuang.openfeign.feign.UserClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/6/14 11:05
 */
@Component
public class UserClientFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public User save(User user) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public User saveByForm(User user) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public User saveByParam(String name, Integer age) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public User update(Long id, String name, Integer age) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public User update(Long id, User user) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public User updateByForm(User user) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public User updateByParam(Long id, User user) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public Boolean delete(Long id) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public User getUser(Long id) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public List<User> getUsers() {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public List<User> getUsersByPage(Integer current, Integer size, Long id, String name, Integer age) {
                throw new BusinessException("openfeign exception");
            }

            @Override
            public Boolean uploadFile(MultipartFile file) {
                throw new BusinessException("openfeign exception");
            }
        };
    }
}
