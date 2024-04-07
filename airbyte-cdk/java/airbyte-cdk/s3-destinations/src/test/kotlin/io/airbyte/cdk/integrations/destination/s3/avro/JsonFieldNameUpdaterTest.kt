/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */
package io.airbyte.cdk.integrations.destination.s3.avro

import com.fasterxml.jackson.databind.JsonNode
import io.airbyte.commons.json.Jsons.deserialize
import io.airbyte.commons.resources.MoreResources.readResource
import io.airbyte.commons.util.MoreIterators.toList
import java.io.IOException
import java.util.function.Function
import java.util.stream.Collectors
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class JsonFieldNameUpdaterTest {
    @Test
    @Throws(IOException::class)
    fun testFieldNameUpdate() {
        val testCases = deserialize(readResource("parquet/json_field_name_updater/test_case.json"))
        for (testCase in testCases) {
            val nameMap = testCase["nameMap"]
            val nameUpdater =
                JsonFieldNameUpdater(
                    toList(nameMap.fields())
                        .stream()
                        .collect(
                            Collectors.toMap(
                                Function { obj: Map.Entry<String, JsonNode> -> obj.key },
                                Function { e: Map.Entry<String, JsonNode> -> e.value.asText() }
                            )
                        )
                )

            val original = testCase["original"]
            val updated = testCase["updated"]

            Assertions.assertEquals(original, nameUpdater.getJsonWithOriginalFieldNames(updated))
        }
    }
}
