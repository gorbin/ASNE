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

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.VKSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;

/**
 * Universal data list for VK API.
 * This class is not thread-safe.
 * @param <T> type of stored values.
 * @see <a href="http://vk.com/dev/list">http://vk.com/dev/list</a>
 */
@SuppressWarnings({"unchecked", "UnusedDeclaration"})
public class VKList<T extends VKApiModel & Parcelable & Identifiable> extends VKApiModel implements java.util.List<T>,Parcelable {

    /**
     * The server did not return the count field.
     */
    private final static int NO_COUNT = -1;

    /**
     * Decorated list
     */
    private ArrayList<T> items = new ArrayList<T>();

    /**
     * Field {@code count} which returned by server.
     */
    private int count = NO_COUNT;

    /**
     * Creates empty list.
     */
    public VKList() {

    }

    /**
     * Creates list and fills it according with given data.
     */
    public VKList(java.util.List<? extends T> data) {
        assert data != null;
        items = new ArrayList<T>(data);
    }

    /**
     * Creates list and fills it according with data in {@code from}.
     * @param from an object that represents a list adopted in accordance with VK API format. You can use null.
     * @param clazz class represents a model that has a public constructor with {@link org.json.JSONObject} argument.
     */
    public VKList(JSONObject from, Class<? extends T> clazz) {
        fill(from, clazz);
    }

    /**
     * Creates list and fills it according with data in {@code from}.
     * @param from an array of items in the list. You can use null.
     * @param clazz class represents a model that has a public constructor with {@link org.json.JSONObject} argument.
     */
    public VKList(JSONArray from, Class<? extends T> clazz) {
        fill(from, clazz);
    }

    /**
     * Creates list and fills it according with data in {@code from}.
     * @param from an object that represents a list adopted in accordance with VK API format. You can use null.
     * @param creator interface implementation to parse objects.
     */
    public VKList(JSONObject from, Parser<T> creator) {

        fill(from, creator);
    }

    /**
     * Creates list and fills it according with data in {@code from}.
     * @param from an array of items in the list. You can use null.
     * @param creator interface implementation to parse objects.
     */
    public VKList(JSONArray from, Parser<T> creator) {

        fill(from, creator);
    }

    /**
     * Fills list according with data in {@code from}.
     * @param from an object that represents a list adopted in accordance with VK API format. You can use null.
     * @param clazz class represents a model that has a public constructor with {@link org.json.JSONObject} argument.
     */
    public void fill(JSONObject from, Class<? extends T> clazz) {
        if (from.has("response")) {
            JSONArray array = from.optJSONArray("response");
            if (array != null) {
                fill(array, clazz);
            }
            else {
                fill(from.optJSONObject("response"), clazz);
            }
        } else {
            fill(from, new ReflectParser<T>(clazz));
        }
    }

    /**
     * Creates list and fills it according with data in {@code from}.
     * @param from an array of items in the list. You can use null.
     * @param clazz class represents a model that has a public constructor with {@link org.json.JSONObject} argument.
     */
    public void fill(JSONArray from, Class<? extends T> clazz) {
        fill(from, new ReflectParser<T>(clazz));
    }

    /**
     * Fills list according with data in {@code from}.
     * @param from an object that represents a list adopted in accordance with VK API format. You can use null.
     * @param creator interface implementation to parse objects.
     */
    public void fill(JSONObject from, Parser<? extends T> creator) {
        if(from != null) {
            fill(from.optJSONArray("items"), creator);
            count = from.optInt("count", count);
        }
    }

    /**
     * Fills list according with data in {@code from}.
     * @param from an array of items in the list. You can use null.
     * @param creator interface implementation to parse objects.
     */
    public void fill(JSONArray from, Parser<? extends T> creator) {
        if(from != null) {
            for(int i = 0; i < from.length(); i++) {
                try {
                    T object = creator.parseObject(from.getJSONObject(i));
                    if(object != null) {
                        items.add(object);
                    }
                } catch (Exception e) {
                    if (VKSdk.DEBUG)
                        e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds the element before the element with the specified id.
     * If an element with the specified id is not found, adds an element to the end of the list.
     * @param id element identifier to add element before it.
     * @param data element to add
     */
    public void addBefore(int id, T data) {
        int size = size();
        for(int i = 0; i < size; i++)  {
            if(get(i).getId() > id || i == size - 1) {
                add(i, data);
                break;
            }
        }
    }

    /**
     * Adds the element after the element with the specified id.
     * If an element with the specified id is not found, adds an element to the end of the list.
     * @param id element identifier to add element after it.
     * @param data element to add
     */
    public void addAfter(int id, T data) {
        int size = size();
        for(int i = 0; i < size; i++)  {
            if(get(i).getId() > id || i == size - 1) {
                add(i + 1, data);
                break;
            }
        }
    }

    /**
     * Returns element according with id.
     * If nothing found, returns null.
     */
    public T getById(int id) {
        for(T item: this) {
            if(item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Searches through the list of available items. <br />
     * <br />
     * The search will be carried out not by the content of characters per line, and the content of them in separate words. <br />
     * <br />
     * Search is not case sensitive.  <br />
     * <br />
     * To support search class {@code T} must have overridden method {@link #toString()},
     * search will be carried out exactly according to the result of calling this method. <br />
     * <br />
     * <br />
     * Suppose there are elements in the list of contents:
     * <code><pre>
     * - Hello world
     * - Hello test
     * </pre></code>
     * In this case, the matches will be on search phrases {@code 'Hel'}, {@code 'Hello'}, {@code 'test'}, but not on {@code 'llo'}, {@code 'llo world'}
     *
     * @param query search query can not be equal to {@code null}, but can be an empty string.
     * @return created based on the search results new list. If no matches are found, the list will be empty.
     */
    public VKList<T> search(String query) {
        VKList<T> result = new VKList<T>();
        final Pattern pattern = Pattern.compile("(?i).*\\b" + query + ".*");
        for (T item : this) {
            if (pattern.matcher(item.toString()).find()) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Returns the return value of the field VK API {@code count}, if it has been returned, and the size of the list, if not.
     */
    public int getCount() {
        return count != NO_COUNT ? count : size();
    }

    @Override
    public void add(int location, T object) {
        items.add(location, object);
    }

    @Override
    public boolean add(T object) {
        return items.add(object);
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        return items.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return items.addAll(collection);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public boolean contains(Object object) {
        return items.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        assert collection != null;
        return items.containsAll(collection);
    }

    @Override
    public boolean equals(Object object) {
        return ((Object) this).getClass().equals(object.getClass()) && items.equals(object);
    }

    @Override
    public T get(int location) {
        return items.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return items.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return items.lastIndexOf(object);
    }


    @Override
    public ListIterator<T> listIterator() {
        return items.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int location) {
        return items.listIterator(location);
    }

    @Override
    public T remove(int location) {
        return items.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return items.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        assert collection != null;
        return items.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return items.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return items.set(location, object);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public java.util.List<T> subList(int start, int end) {
        return items.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        assert array != null;
        return items.toArray(array);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(items.size());
        for(T item: this) {
            dest.writeParcelable(item, flags);
        }
        dest.writeInt(this.count);
    }

    /**
     * Creates list from Parcel
     */
    public VKList(Parcel in) {
        int size = in.readInt();
        for(int i = 0; i < size; i++) {
            items.add( ((T) in.readParcelable(((Object) this).getClass().getClassLoader())));
        }
        this.count = in.readInt();
    }

    public static Creator<VKList> CREATOR = new Creator<VKList>() {
        public VKList createFromParcel(Parcel source) {
            return new VKList(source);
        }

        public VKList[] newArray(int size) {
            return new VKList[size];
        }
    };

    /**
     * Used when parsing the list objects as interator created from {@link org.json.JSONArray} a instances of items of the list.
     * @param <D> list item type.
     */
    public static interface Parser<D> {

        /**
         * Creates a list item of its representation return VK API from {@link org.json.JSONArray}
         * @param source representation of the object in the format returned by VK API.
         * @return created element to add to the list.
         * @throws Exception if the exception is thrown, the element iterated this method will not be added to the list.
         */
        D parseObject(JSONObject source) throws Exception;
    }

    /**
     * Parser list items using reflection mechanism.
     * To use an object class must have a public constructor that accepts {@link org.json.JSONObject}.
     * If, during the creation of the object constructor will throw any exception, the element will not be added to the list.
     * @param <D> list item type.
     */
    public final static class ReflectParser<D extends VKApiModel> implements Parser<D> {

        private final Class<? extends D> clazz;

        public ReflectParser(Class<? extends D> clazz) {
            this.clazz = clazz;
        }

        @Override
        public D parseObject(JSONObject source) throws Exception {
            return (D) clazz.newInstance().parse(source);
        }
    }

    @Override
    public VKApiModel parse(JSONObject response) throws JSONException {
        throw new JSONException("Operation is not supported while class is generic");
    }
}