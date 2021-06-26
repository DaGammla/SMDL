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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class SMDLAttribute {

    private final String key;
    private final String value;
    private final int depth;

    public SMDLAttribute(@NotNull String string, int depth){

        this.depth = depth;

        var split = string.split(":", 2);

        if (split.length == 2){
            key = split[0].trim();
            var splitTrim = split[1].trim();

            if (splitTrim.equals("")){
                value = null;
            } else {
                value = splitTrim;
            }
        } else {
            key = string.trim();
            value = null;
        }
    }

    public SMDLAttribute(@NotNull String key, @Nullable String value, int depth){
        this.key = key;
        this.value = value;
        this.depth = depth;
    }

    /**
     * Gets the key of this attribute
     * @return the key
     */
    public @NotNull String getKey(){
        return key;
    }

    /**
     * Returns whether this attribute has an assigned value
     * @return true, if this attribute has a value, false otherwise
     */
    public boolean hasValue(){
        return value != null;
    }

    /**
     * Gets this attributes value as a String
     * @return this attributes value as a String
     * @throws SMDLNoValueException when no value is present
     */
    public @NotNull String stringValue() throws SMDLNoValueException {
        if (value == null)
            throw new SMDLNoValueException("Attribute has no value");
        return value;
    }

    /**
     * Gets this attributes value as a String, if present
     * @return this attributes value as a String or null when none present
     */
    public @Nullable String tryStringValue(){
        return value;
    }

    /**
     * Gets this attributes value as a BigDecimal
     * @return this attributes value as a BigDecimal
     * @throws RuntimeException when no value is present or the value is no valid number
     */
    public @NotNull BigDecimal decimalValue() throws RuntimeException {
        return new BigDecimal(value);
    }

    /**
     * Gets this attributes value as a BigDecimal, if present
     * @return this attributes value as a BigDecimal or null when none present
     */
    public @Nullable BigDecimal tryDecimalValue(){
        try {
            return decimalValue();
        } catch (RuntimeException e){
            return null;
        }
    }

    /**
     * Gets this attributes value as a BigInteger
     * @return this attributes value as a BigInteger
     * @throws RuntimeException when no value is present or the value is no valid number
     */
    public @NotNull BigInteger integerValue() throws RuntimeException {
        return new BigInteger(value);
    }

    /**
     * Gets this attributes value as a BigInteger, if present
     * @return this attributes value as a BigInteger or null when none present
     */
    public @Nullable BigInteger tryIntegerValue(){
        try {
            return integerValue();
        } catch (RuntimeException e){
            return null;
        }
    }

    /**
     * Gets this attributes value as a SMDL Object
     * @return this attributes value as a SMDL Object
     * @throws SMDLParseException when no value is present or when the value could be parsed
     */
    public @NotNull SMDLObject smdlValue() throws SMDLParseException {
        return SMDL.parse(value, depth + 1);
    }

    /**
     * Gets this attributes value as a SMDL Object, if present
     * @return this attributes value as a SMDL Object or null when none present
     */
    public @Nullable SMDLObject trySmdlValue(){
        try {
            return smdlValue();
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public @NotNull String toString() {
        return toString(0);
    }

    public @NotNull String toString(int depth) {
        if (value == null){
            return key;
        }

        var smdlVal = trySmdlValue();

        if (smdlVal != null){
            return key + ": " + smdlVal.toString(depth + 1);
        } else {
            return key + ": " + value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SMDLAttribute that = (SMDLAttribute) o;
        return key.equals(that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
