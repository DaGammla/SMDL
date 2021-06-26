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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMDL {

    private SMDL() {}

    /**
     * Parses the input into a SMDL Object
     * @param s the string to be parsed
     * @return an SMDL Object containing all attributes given in s
     * @throws SMDLParseException when no correct SMDL Object could be parsed
     */
    public static @NotNull SMDLObject parse(@NotNull String s) throws SMDLParseException{
        Pattern eqPat = Pattern.compile("&+");
        Matcher eqMat = eqPat.matcher(s);
        if (!eqMat.find()){
            throw new SMDLParseException("No paragraphs defined for a SMDL object");
        }
        int depth = eqMat.end() - eqMat.start() - 1;

        return parse(s, depth);
    }

    /**
     * Parses the input into a SMDL Object but only if it is of the specified depth
     * @param s the string to be parsed
     * @param depth the depth for which the parser should look for
     * @return an SMDL Object containing all attributes given in s
     * @throws SMDLParseException when no correct SMDL Object could be parsed
     */
    public static @NotNull SMDLObject parse(@NotNull String s, int depth) throws SMDLParseException{

        Pattern eqPat = Pattern.compile("&+");
        Matcher eqMat = eqPat.matcher(s);
        if (!eqMat.find()){
            throw new SMDLParseException("No paragraphs defined for a SMDL object");
        }
        int detectedDepth = eqMat.end() - eqMat.start() - 1;

        if (depth != detectedDepth){
            throw new SMDLParseException("Incorrect depth");
        }

        var smdl = create();

        var trimmed = s.trim();

        Pattern pattern = Pattern.compile("([^&\\\\]|^)&{" + (depth + 1) +"}([^&]|$)");
        Matcher matcher = pattern.matcher(trimmed);

        var firstParagraphMatch = matcher.find();

        if (!firstParagraphMatch){
            throw new SMDLParseException("No paragraphs defined for a SMDL object in depth " + depth + " (" + "&".repeat(depth + 1) + ")");
        }

        var startIndex = matcher.end() - 1;

        var stringParts = new ArrayList<String>();

        for (var nextMatch = matcher.find(matcher.start() + 1); nextMatch; nextMatch = matcher.find(matcher.start() + 1)) {
            var endIndex = matcher.start();

            stringParts.add(trimmed.substring(startIndex, endIndex + 1));

            startIndex = matcher.end() - 1;
        }

        stringParts.add(trimmed.substring(startIndex));

        for (var stringPart:stringParts) {
            var attribute = new SMDLAttribute(stringPart, depth);
            if (!smdl.add(attribute))
                throw new SMDLParseException("Attribute key assigned multiple times");
        }

        return smdl;
    }

    /**
     * Creates an empty SMDL Object which can then be assigned attributes
     * @return a new SMDL Object without any attributes
     */
    public static @NotNull SMDLObject create(){
        return new SMDLObject();
    }

    /**
     * Try to parse the input into a SMDL Object without throwing an Exception
     * @param s the string to be parsed
     * @return the parsed SMDL Object or null when an SMDLParseException occurs
     */
    public static @Nullable SMDLObject tryParse(@Nullable String s){
        if (s == null)
            return null;
        try {
            return parse(s);
        } catch (SMDLParseException e){
            return null;
        }
    }
}
