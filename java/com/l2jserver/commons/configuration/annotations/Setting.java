/*
 * Copyright (C) 2004-2019 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.commons.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by Mangol on 25.11.2015.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Setting
{
	/**
	 * Задает имя переменной
	 */
	String name() default "";
	
	/**
	 * Игнорирует переменную пропуская ее.
	 */
	boolean ignore() default false;
	
	/**
	 * По дефолту разделитель ";" P.S. В качестве разделителя массива не использовать пробелов (\s+, \t, )
	 */
	String splitter() default ";";
	
	/**
	 * Устанавливает минимальное значения В массиве для каждого из значений. Используется только для переменных int[], double[], long[], int, double, long
	 */
	double minValue() default -1;
	
	/**
	 * Устанавливает максимальное значения В массиве для каждого из значений. Используется только для переменных int[], double[], long[], int, double, long
	 */
	double maxValue() default -1;
	
	/**
	 * Может принимать значение Нулл ? Если вдруг значение в конфиге не проставлено нарошно например -
	 * <p/>
	 * # Можно выключить некоторые мультиселлы "DisabledMultisells" : null, (а должен быть к примеру массив 1;2;3;4;5)
	 * <p/>
	 * , а canNull - true, тогда лог не сообщит о том что переменная пуста. Обязательно заполнить переменную в виде:
	 * @Setting(name = "DisabledMultisells", canNull = true) public static int[] ALT_DISABLED_MULTISELL = new int[0];
	 *               <p/>
	 *               Иначе можем получить ошибку при использовании переменной.
	 */
	boolean canNull() default false;
	
	/**
	 * Умножает значение на целые числа.
	 */
	double increase() default 1;
	
	/**
	 * Указывает рефлекшену что переменная будет заполняться через метод, передавая ему значение. Пример :
	 * @Setting(name = "GMNameColour", method = "gmNameColour") public static int GM_NAME_COLOUR;
	 *               <p/>
	 *               Метод всегда должен передавать лишь 1 переменную.
	 *               <p/>
	 *               Пример: private void gmNameColour(final String value) - может быть как String, Long, Int, Double, Boolean, Float, Short, Byte, так же может принимать массив([]). { GM_NAME_COLOUR = Integer.decode("0x" + value); }
	 */
	String method() default "";
}
