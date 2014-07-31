//
//  Copyright (c) 2014 VK.com
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of
//  this software and associated documentation files (the "Software"), to deal in
//  the Software without restriction, including without limitation the rights to
//  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
//  the Software, and to permit persons to whom the Software is furnished to do so,
//  subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
//  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
//  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
//  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.vk.sdk.api.model;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

/**
 * Collection of helpers to parse server responses.
 */
class ParseUtils {

    private ParseUtils() {
    }

    /**
     * Parse boolean from server response.
     *
     * @param from server response like this format: {@code response: 1}
     * @throws JSONException if server response is not valid
     */
    public static boolean parseBoolean(String from) throws JSONException {
        return new JSONObject(from).optInt("response", 0) == 1;
    }

    /**
     * Parse boolean from JSONObject with given name.
     *
     * @param from server response like this format: {@code field: 1}
     * @param name name of field to read
     */
    public static boolean parseBoolean(JSONObject from, String name) {
        return from != null && from.optInt(name, 0) == 1;
    }

    /**
     * Parse int from JSONObject with given name.
     *
     * @param from server response like this format: {@code field: 34}
     * @param name name of field to read
     */
    public static int parseInt(JSONObject from, String name) {
        if (from == null) return 0;
        return from.optInt(name, 0);
    }

    /**
     * Parse int from server response.
     *
     * @param from server response like this format: {@code response: 34}
     * @throws JSONException if server response is not valid
     */
    public static int parseInt(String from) throws JSONException {
        if (from == null) return 0;
        return new JSONObject(from).optInt("response");
    }

    /**
     * Parse long from JSONObject with given name.
     *
     * @param from server response like this format: {@code field: 34}
     * @param name name of field to read
     */
    public static long parseLong(JSONObject from, String name) {
        if (from == null) return 0;
        return from.optLong(name, 0);
    }

    /**
     * Parse int array from JSONObject with given name.
     *
     * @param from int JSON array like this one {@code {11, 34, 42}}
     */
    public static int[] parseIntArray(JSONArray from) {
        int[] result = new int[from.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = from.optInt(i);
        }
        return result;
    }

    /**
     * Returns root JSONObject from server response
     *
     * @param source standart VK server response
     * @throws JSONException if source is not valid
     */
    public static JSONObject rootJSONObject(String source) throws JSONException {
        return new JSONObject(source).getJSONObject("response");
    }

    /**
     * Returns root JSONArray from server response
     *
     * @param source standart VK server response
     * @throws JSONException if source is not valid
     */
    public static JSONArray rootJSONArray(String source) throws JSONException {
        return new JSONObject(source).getJSONArray("response");
    }

    /**
     * Parses object with follow rules:
     *
     * 1. All fields should had a public access.
     * 2. The name of the filed should be fully equal to name of JSONObject key.
     * 3. Supports parse of all Java primitives, all {@link java.lang.String},
     *  arrays of primitive types, {@link java.lang.String}s and {@link com.vk.sdk.api.model.VKApiModel}s,
     *  list implementation line {@link com.vk.sdk.api.model.VKList}, {@link com.vk.sdk.api.model.VKAttachments.VKAttachment} or {@link com.vk.sdk.api.model.VKPhotoSizes},
     *  {@link com.vk.sdk.api.model.VKApiModel}s.
     *
     * 4. Boolean fields defines by vk_int == 1 expression.
     *
     * @param object object to initialize
     * @param source data to read values
     * @param <T> type of result
     * @return initialized according with given data object
     * @throws JSONException if source object structure is invalid
     */
    @SuppressWarnings("rawtypes")
    public static <T> T parseViaReflection(T object, JSONObject source) throws JSONException {
        if (source.has("response")) {
            source = source.optJSONObject("response");
        }
        if (source == null) {
            return object;
        }
        for (Field field : object.getClass().getFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            Object value = source.opt(fieldName);
            if (value == null) {
                continue;
            }
            try {
                if (fieldType.isPrimitive() && value instanceof Number) {
                    Number number = (Number) value;
                    if (fieldType.equals(int.class)) {
                        field.setInt(object, number.intValue());
                    } else if (fieldType.equals(long.class)) {
                        field.setLong(object, number.longValue());
                    } else if (fieldType.equals(float.class)) {
                        field.setFloat(object, number.floatValue());
                    } else if (fieldType.equals(double.class)) {
                        field.setDouble(object, number.doubleValue());
                    } else if (fieldType.equals(boolean.class)) {
                        field.setBoolean(object, number.intValue() == 1);
                    } else if (fieldType.equals(short.class)) {
                        field.setShort(object, number.shortValue());
                    } else if (fieldType.equals(byte.class)) {
                        field.setByte(object, number.byteValue());
                    }
                } else {
                    Object result = field.get(object);
                    if (value.getClass().equals(fieldType)) {
                        result = value;
                    } else if (fieldType.isArray() && value instanceof JSONArray) {
                        result = parseArrayViaReflection((JSONArray) value, fieldType);
                    }  else if(VKPhotoSizes.class.isAssignableFrom(fieldType) && value instanceof JSONArray) {
                        Constructor<?> constructor = fieldType.getConstructor(JSONArray.class);
                        result = constructor.newInstance((JSONArray) value);
                    } else if(VKAttachments.class.isAssignableFrom(fieldType) && value instanceof JSONArray) {
                        Constructor<?> constructor = fieldType.getConstructor(JSONArray.class);
                        result = constructor.newInstance((JSONArray) value);
                    } else if(VKList.class.equals(fieldType)) {
                        ParameterizedType genericTypes = (ParameterizedType) field.getGenericType();
                        Class<?> genericType = (Class<?>) genericTypes.getActualTypeArguments()[0];
                        if(VKApiModel.class.isAssignableFrom(genericType) && Parcelable.class.isAssignableFrom(genericType) && Identifiable.class.isAssignableFrom(genericType)) {
                            if(value instanceof JSONArray) {
                                result = new VKList((JSONArray) value, genericType);
                            } else if(value instanceof JSONObject) {
                                result = new VKList((JSONObject) value, genericType);
                            }
                        }
                    } else if (VKApiModel.class.isAssignableFrom(fieldType) && value instanceof JSONObject) {
                        result = ((VKApiModel) fieldType.newInstance()).parse((JSONObject) value);
                    }
                    field.set(object, result);
                }
            } catch (InstantiationException e) {
                throw new JSONException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new JSONException(e.getMessage());
            } catch (NoSuchMethodException e) {
                throw new JSONException(e.getMessage());
            } catch (InvocationTargetException e) {
                throw new JSONException(e.getMessage());
            } catch (NoSuchMethodError e) {
                // Примечание Виталия:
                // Вы не поверите, но у некоторых вендоров getFields() вызывает ВОТ ЭТО.
                // Иногда я всерьез задумываюсь, правильно ли я поступил, выбрав Android в качестве платформы разработки.
                throw new JSONException(e.getMessage());
            }
        }
        return object;
    }

    /**
     * Parses array from given JSONArray.
     * Supports parsing of primitive types and {@link com.vk.sdk.api.model.VKApiModel} instances.
     * @param array JSONArray to parse
     * @param arrayClass type of array field in class.
     * @return object to set to array field in class
     * @throws JSONException if given array have incompatible type with given field.
     */
    private static Object parseArrayViaReflection(JSONArray array, Class arrayClass) throws JSONException {
        Object result = Array.newInstance(arrayClass.getComponentType(), array.length());
        Class<?> subType = arrayClass.getComponentType();
        for (int i = 0; i < array.length(); i++) {
            try {
                Object item = array.opt(i);
                if(VKApiModel.class.isAssignableFrom(subType) && item instanceof JSONObject) {
                    VKApiModel model = (VKApiModel) subType.newInstance();
                    item = model.parse((JSONObject) item);
                }
                Array.set(result, i, item);
            } catch (InstantiationException e) {
                throw new JSONException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new JSONException(e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new JSONException(e.getMessage());
            }
        }
        return result;
    }
}
