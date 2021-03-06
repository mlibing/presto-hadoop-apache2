/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.hadoop;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.PrestoFileSystemCache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class HadoopFileSystemCache
{
    private static PrestoFileSystemCache cache;

    private HadoopFileSystemCache() {}

    public static synchronized void initialize()
    {
        if (cache == null) {
            cache = setFinalStatic(FileSystem.class, "CACHE", new PrestoFileSystemCache());
        }
    }

    private static <T> T setFinalStatic(Class<?> clazz, String name, T value)
    {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, value);

            return value;
        }
        catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
