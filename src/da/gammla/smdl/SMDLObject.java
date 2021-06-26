/*
 *    Copyright 2021 DaGammla
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package da.gammla.smdl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SMDLObject {

    private final HashMap<String, SMDLAttribute> attributes;
    private final LinkedList<@NotNull SMDLAttribute> attributesOrdered;
    private final int depth;

    public SMDLObject(){
        attributes = new HashMap<>();
        attributesOrdered = new LinkedList<>();
        depth = 0;
    }

    public boolean removeAttribute(@NotNull String key){
        if (hasAttribute(key)){

            var att= attributes.remove(key);
            attributesOrdered.remove(att);

            return true;
        } else {
            return false;
        }
    }

    public void put(@NotNull String key, @Nullable String value){

        if (hasAttribute(key)){
            removeAttribute(key);
        }

        if (value != null && value.trim().equals("")){
            value = null;
        }

        var smdlTry = SMDL.tryParse(value);

        if (smdlTry != null && smdlTry.depth -1 != this.depth){
            value = smdlTry.toString(this.depth + 1);
        }

        var attr = new SMDLAttribute(key, value, depth + 1);

        attributes.put(key, attr);
        attributesOrdered.add(attr);

    }

    public void put(@NotNull String key, @Nullable Object value){
        put(key, value != null ? value.toString() : null);
    }

    public boolean add(@NotNull SMDLAttribute attribute){
        var key = attribute.getKey();
        if (hasAttribute(key)){
            return false;
        } else {
            attributes.put(key, attribute);
            attributesOrdered.add(attribute);
            return true;
        }
    }

    public @NotNull SMDLAttribute[] getAttributes(){
        return attributesOrdered.toArray(new SMDLAttribute[0]);
    }

    public boolean hasAttribute(@NotNull String key){
        return attributes.containsKey(key);
    }

    public @Nullable SMDLAttribute getAttribute(@NotNull String key){
        return attributes.get(key);
    }

    public @NotNull String getString(@NotNull String key){
        return attributes.get(key).stringValue();
    }

    public @Nullable String tryGetString(@NotNull String key){
        return attributes.get(key).tryStringValue();
    }

    public float getFloat(@NotNull String key){
        return attributes.get(key).decimalValue().floatValue();
    }

    public double getDouble(@NotNull String key){
        return attributes.get(key).decimalValue().doubleValue();
    }

    public int getInt(@NotNull String key){
        return attributes.get(key).integerValue().intValueExact();
    }

    public long getLong(@NotNull String key){
        return attributes.get(key).integerValue().longValueExact();
    }

    public @NotNull SMDLObject getSmdlValue(@NotNull String key) throws SMDLParseException {
        return attributes.get(key).smdlValue();
    }

    public @Nullable SMDLObject tryGetSmdlValue(@NotNull String key) {
        return attributes.get(key).trySmdlValue();
    }

    @Override
    public @NotNull String toString() {
        return toString(depth);
    }

    public @NotNull String toString(int depth){
        var sb = new StringBuilder();

        if (depth > 0){
            sb.append(System.lineSeparator());
        }

        for (var attribute : attributesOrdered) {
            sb.append(tabs(depth));
            sb.append(equSign(depth));
            sb.append(" ");
            sb.append(attribute.toString(depth));
            sb.append(System.lineSeparator());
        }

        return sb.toString().stripTrailing();
    }

    private static String equSign(int depth){
        return "&".repeat(depth + 1);
    }

    private static String tabs(int depth){
        return "  ".repeat(depth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SMDLObject that = (SMDLObject) o;
        return attributes.equals(that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes);
    }
}
