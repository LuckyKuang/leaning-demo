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

package com.luckykuang.adapter.adapter.animal;

import com.luckykuang.adapter.target.animal.CatAnimalTarget;
import com.luckykuang.adapter.target.animal.DogAnimalAdaptee;

/**
 * 双向适配器
 * @author luckykuang
 * @date 2023/10/12 16:55
 */
public class AnimalAdapter implements CatAnimalTarget, DogAnimalAdaptee {

    private CatAnimalTarget catAnimalTarget;
    private DogAnimalAdaptee dogAnimalAdaptee;

    public void setCatAnimalTarget(CatAnimalTarget catAnimalTarget) {
        this.catAnimalTarget = catAnimalTarget;
    }

    public void setDogAnimalAdaptee(DogAnimalAdaptee dogAnimalAdaptee) {
        this.dogAnimalAdaptee = dogAnimalAdaptee;
    }

    @Override
    public void catSound() {
        catAnimalTarget.catSound();
    }

    @Override
    public void catAction() {
        catAnimalTarget.catAction();
    }

    @Override
    public void dogSound() {
        dogAnimalAdaptee.dogSound();
    }

    @Override
    public void dogAction() {
        dogAnimalAdaptee.dogAction();
    }
}
