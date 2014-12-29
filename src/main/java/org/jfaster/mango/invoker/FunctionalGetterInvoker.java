/*
 * Copyright 2014 mango.jfaster.org
 *
 * The Mango Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.jfaster.mango.invoker;

import org.jfaster.mango.exception.UncheckedException;
import org.jfaster.mango.reflect.TypeToken;
import org.jfaster.mango.reflect.Types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 函数式getter方法调用器
 *
 * @author ash
 */
public class FunctionalGetterInvoker extends FunctionalInvoker implements GetterInvoker {

    private TypeToken<?> returnToken;
    private TypeToken<?> realReturnToken;

    private FunctionalGetterInvoker(String name, Method method) {
        super(name, method);
        returnToken = realReturnToken = TypeToken.of(method.getGenericReturnType());
        if (functional) {
            if (function.checkType() && // 需要检测type
                    !Types.isTypeAssignable(inputToken.getType(), returnToken.getType())) {
                throw new ClassCastException("function[" + function.getClass() + "] " +
                        "on method[" + method + "] error, function's inputType[" + inputToken.getType() + "] " +
                        "must be assignable from method's returnType[" + returnToken.getType() + "]");
            }
            returnToken = outputToken;
        }
    }

    public static FunctionalGetterInvoker create(String name, Method method) {
        return new FunctionalGetterInvoker(name, method);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object object) {
        try {
            Object input = method.invoke(object);
            Object r = function.apply(input, realReturnToken);
            return r;
        } catch (IllegalAccessException e) {
            throw new UncheckedException(e.getMessage(), e.getCause());
        } catch (InvocationTargetException e) {
            throw new UncheckedException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public boolean isIdentity() {
        return false;
    }

    @Override
    public Type getType() {
        return returnToken.getType();
    }

    @Override
    public Class<?> getRawType() {
        return returnToken.getRawType();
    }

}
