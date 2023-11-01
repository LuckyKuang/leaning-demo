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

package com.luckykuang.adapter.adapter.motor;

import com.luckykuang.adapter.target.motor.MotorTarget;
import com.luckykuang.adapter.target.motor.adaptee.ElectricMotor;

/**
 * 电能发动机适配者实现
 * @author luckykuang
 * @date 2023/10/12 14:10
 */
public class ElectricMotorAdapter implements MotorTarget {

    private final ElectricMotor electricMotor;

    public ElectricMotorAdapter(ElectricMotor electricMotor){
        this.electricMotor = electricMotor;
    }
    @Override
    public void drive() {
        electricMotor.drive();;
    }
}
