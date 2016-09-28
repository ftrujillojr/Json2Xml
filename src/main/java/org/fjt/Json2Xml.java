package org.fjt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
public final class Json2Xml {

    private String jsonStr;
    private StringBuilder xmlStr;
    private int indentBeginLevel;

    public Json2Xml() {
        xmlStr = new StringBuilder();
        this.indentBeginLevel = 0;
    }

    /**
     * toXML(jsonStr) will convert a Json string into XML.
     *
     * @param jsonStr
     * @return String XML
     * @throws Json2XmlException
     */
    public String toXML(String jsonStr) throws Json2XmlException {
        xmlStr.setLength(0); // erases last value.
        try {
            this.validateJsonString(jsonStr);
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonTree = jsonParser.parse(jsonStr);
            this.processJsonElement(jsonTree, "");
        } catch (Json2XmlException ex) {
            throw ex;
        }

        return xmlStr.toString();
    }

    /**
     * validateJsonString() method will validate that a JSON string does not
     * have missing commas, quotes, etc...
     *
     * @param jsonStr
     * @throws Json2XmlException
     */
    private void validateJsonString(String jsonStr) throws Json2XmlException {
        try {
            new JsonParser().parse(jsonStr);
            this.jsonStr = jsonStr;
        } catch (JsonSyntaxException ex) {
            throw new Json2XmlException("ERROR: Input jsonString was not valid!\n" + jsonStr);
        }
    }

    /**
     * processJsonElement() is a recursive method to walk the Json tree.
     *
     * @param jsonElement
     * @param arrayKey
     */
    private void processJsonElement(JsonElement jsonElement, String arrayKey) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                JsonElement nextJsonElement = entry.getValue();

                if (nextJsonElement.isJsonNull()) {
                    this.indentBeginObject(key);
                    this.xmlStr.append("null");
                    this.indentEndObject(key);
                } else if (nextJsonElement.isJsonPrimitive()) {
                    this.indentBeginObject(key);
                    this.xmlStr.append(nextJsonElement.getAsString());
                    this.indentEndObject(key);
                } else if (nextJsonElement.isJsonObject()) {
                    this.indentBeginObject("object");
                    this.indentBeginLevel++;
                    this.processJsonElement(nextJsonElement, key);
                    if (this.indentBeginLevel > 0) {
                        this.indentBeginLevel--;
                    }
                    this.xmlStr.append("\n");
                    this.indentEndObject("object");
                } else if (nextJsonElement.isJsonArray()) {
                    int size = nextJsonElement.getAsJsonArray().size();
                    this.indentBeginArray("array", size);
                    this.indentBeginLevel++;
                    this.processJsonElement(nextJsonElement, key);
                    if (this.indentBeginLevel > 0) {
                        this.indentBeginLevel--;
                    }
                    this.indentEndArray("array");
                }
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Iterator<JsonElement> itr = jsonArray.iterator();

            while (itr.hasNext()) {
                JsonElement nextJsonElement = itr.next();

                if (nextJsonElement.isJsonNull()) {
                    this.indentBeginObject(arrayKey);
                    this.xmlStr.append("null");
                    this.indentEndObject(arrayKey);
                } else if (nextJsonElement.isJsonPrimitive()) {
                    this.indentBeginObject(arrayKey);
                    this.xmlStr.append(nextJsonElement.getAsString());
                    this.indentEndObject(arrayKey);
                } else if (nextJsonElement.isJsonObject() || nextJsonElement.isJsonArray()) {
                    this.indentBeginArray(arrayKey, -1);
                    this.indentBeginLevel++;
                    this.processJsonElement(nextJsonElement, "");
                    if (this.indentBeginLevel > 0) {
                        this.indentBeginLevel--;
                    }
                    this.indentEndArray(arrayKey);
                }
            }
        }
    }

    private void indentBeginArray(String key, int size) {
        this.xmlStr.append("\n");
        for (int ii = 0; ii < this.indentBeginLevel; ii++) {
            this.xmlStr.append("    ");
        }
        if (size == -1) {
            this.xmlStr.append("<").append(key).append(">");
        } else {
            this.xmlStr.append("<").append(key).append(" size=").append(size).append(">");
        }
    }

    private void indentEndArray(String key) {
        this.xmlStr.append("\n");
        for (int ii = 0; ii < this.indentBeginLevel; ii++) {
            this.xmlStr.append("    ");
        }
        this.xmlStr.append("</").append(key).append(">");
    }

    private void indentBeginObject(String key) {
        this.xmlStr.append("\n");
        for (int ii = 0; ii < this.indentBeginLevel; ii++) {
            this.xmlStr.append("    ");
        }
        this.xmlStr.append("<").append(key).append(">");
    }

    private void indentEndObject(String key) {
        this.xmlStr.append("</").append(key).append(">");
    }

}
