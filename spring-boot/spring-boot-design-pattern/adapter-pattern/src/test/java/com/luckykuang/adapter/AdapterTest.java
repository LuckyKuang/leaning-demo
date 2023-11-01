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

package com.luckykuang.adapter;

import com.luckykuang.adapter.adapter.animal.AnimalAdapter;
import com.luckykuang.adapter.adapter.motor.ElectricMotorAdapter;
import com.luckykuang.adapter.adapter.motor.OpticalMotorAdapter;
import com.luckykuang.adapter.adapter.player.MP3PlayerAdapter;
import com.luckykuang.adapter.adapter.player.MP4PlayerAdapter;
import com.luckykuang.adapter.adapter.player.WAVPlayerAdapter;
import com.luckykuang.adapter.target.animal.CatAnimalTarget;
import com.luckykuang.adapter.target.animal.DogAnimalAdaptee;
import com.luckykuang.adapter.target.animal.adaptee.CatAnimal;
import com.luckykuang.adapter.target.animal.adaptee.DogAnimal;
import com.luckykuang.adapter.target.motor.MotorTarget;
import com.luckykuang.adapter.target.motor.adaptee.ElectricMotor;
import com.luckykuang.adapter.target.motor.adaptee.OpticalMotor;
import com.luckykuang.adapter.target.player.adaptee.MP3Player;
import com.luckykuang.adapter.target.player.adaptee.MP4Player;
import com.luckykuang.adapter.target.player.adaptee.WAVPlayer;
import org.junit.jupiter.api.Test;

/**
 * @author luckykuang
 * @date 2023/10/13 18:26
 */
class AdapterTest {

    @Test
    public void testAnimalAdapter(){
        // 双向适配器
        AnimalAdapter animalAdapter = new AnimalAdapter();

        // 目标类通过适配器调用适配者方法
        CatAnimalTarget catAnimalTarget = new CatAnimal();
        animalAdapter.setCatAnimalTarget(catAnimalTarget);

        // 适配者通过适配器调用目标类方法
        DogAnimalAdaptee dogAnimalAdaptee = new DogAnimal();
        animalAdapter.setDogAnimalAdaptee(dogAnimalAdaptee);

        // 猫叫狗抓老鼠
        animalAdapter.catSound();
        animalAdapter.dogAction();

        // 狗叫猫抓老鼠
        animalAdapter.dogSound();
        animalAdapter.catAction();
    }

    @Test
    public void testMotorAdapter(){
        // 电能目标接口
        MotorTarget electricMotorTarget = new ElectricMotor();
        electricMotorTarget.drive();

        // 光能目标接口
        MotorTarget opticalMotorTarget = new OpticalMotor();
        opticalMotorTarget.drive();

        // 电能适配者
        ElectricMotor electricMotor = new ElectricMotor();
        // 电能适配器
        ElectricMotorAdapter electricMotorAdapter = new ElectricMotorAdapter(electricMotor);
        electricMotorAdapter.drive();

        // 光能适配者
        OpticalMotor opticalMotor = new OpticalMotor();
        // 电能适配器
        OpticalMotorAdapter opticalMotorAdapter = new OpticalMotorAdapter(opticalMotor);
        opticalMotorAdapter.drive();
    }

    @Test
    public void testPlayerAdapter(){
        // MP3适配者
        MP3Player mp3Player = new MP3Player();
        // MP3适配器
        MP3PlayerAdapter mp3PlayerAdapter = new MP3PlayerAdapter(mp3Player);
        mp3PlayerAdapter.play();

        // MP4适配者
        MP4Player mp4Player = new MP4Player();
        // MP4适配器
        MP4PlayerAdapter mp4PlayerAdapter = new MP4PlayerAdapter(mp4Player);
        mp4PlayerAdapter.play();

        // WAV适配者
        WAVPlayer wavPlayer = new WAVPlayer();
        // WAV适配器
        WAVPlayerAdapter wavPlayerAdapter = new WAVPlayerAdapter(wavPlayer);
        wavPlayerAdapter.play();
    }
}