package com.barchart.pivotal.util;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Joda Time gson codec.
 */
// https://sites.google.com/site/gson/gson-type-adapters-for-common-classes-1
public class GsonDateTime implements JsonSerializer<DateTime>,
		JsonDeserializer<DateTime> {

	@Override
	public JsonElement serialize(final DateTime src, final Type srcType,
			final JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

	@Override
	public DateTime deserialize(final JsonElement json, final Type type,
			final JsonDeserializationContext context) throws JsonParseException {
		return new DateTime(json.getAsString());
	}

}
