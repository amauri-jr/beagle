/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
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

package br.com.zup.beagle.android.context

import androidx.collection.LruCache
import br.com.zup.beagle.android.data.serializer.BeagleMoshi
import br.com.zup.beagle.android.jsonpath.JsonPathFinder
import br.com.zup.beagle.android.jsonpath.JsonPathReplacer
import br.com.zup.beagle.android.logger.BeagleMessageLogs
import br.com.zup.beagle.android.widget.core.Bind
import br.com.zup.beagle.core.ContextData
import br.com.zup.beagle.widget.action.SetContext
import com.squareup.moshi.Moshi
import org.json.JSONArray
import org.json.JSONObject
import java.lang.IllegalStateException

private val EXPRESSION_REGEX = "@\\{([^)]+)\\}".toRegex()

internal data class ContextBinding(
    val context: ContextData,
    val bindings: MutableList<Bind.Expression<*>>
)

internal class ContextDataManager(
    private val jsonPathFinder: JsonPathFinder = JsonPathFinder(),
    private val jsonPathReplacer: JsonPathReplacer = JsonPathReplacer(),
    private val contextPathResolver: ContextPathResolver = ContextPathResolver(),
    private val moshi: Moshi = BeagleMoshi.moshi
) {

    private val lruCache: LruCache<String, Any> = LruCache(20)
    private val contexts: MutableMap<String, ContextBinding> = mutableMapOf()

    fun addContext(contextData: ContextData) {
        contexts[contextData.id] = ContextBinding(
            bindings = mutableListOf(),
            context = contextData
        )
    }

    fun removeContext(contextId: String) {
        contexts.remove(contextId)
    }

    fun addBindingToContext(binding: Bind.Expression<*>) {
        val contextId = binding.getContextId()
        if (contexts.containsKey(contextId)) {
            contexts[contextId]?.bindings?.add(binding)
        }
    }

    fun updateContext(setContext: SetContext): Boolean {
        return contexts[setContext.contextId]?.let { contextBinding ->
            val path = setContext.path ?: contextBinding.context.id
            val setValue = setValue(contextBinding, path, setContext.value)
            if (setValue) {
                evaluateContext(setContext.contextId)
            }
            setValue
        } ?: false
    }

    fun evaluateAllContext() {
        contexts.forEach { entry ->
            notifyBindingChanges(entry.value)
        }
    }

    fun evaluateBinding(bind: Bind.Expression<*>): Any? {
        val contextId = bind.getContextId()

        return contexts[contextId]?.let {
            return evaluateBinding(it.context, bind)
        }
    }

    private fun evaluateBinding(contextData: ContextData, bind: Bind.Expression<*>): Any? {
        val value = getValue(contextData, bind.valueInExpression())

        return try {
            if (value is JSONArray || value is JSONObject) {
                moshi.adapter<Any>(bind.type).fromJson(value.toString()) ?:
                throw IllegalStateException("JSON deserialization returned null")
            } else {
                value ?: throw IllegalStateException("Expression evaluation returned null")
            }
        } catch (ex: Exception) {
            BeagleMessageLogs.errorWhileTryingToNotifyContextChanges(ex)
            null
        }
    }

    private fun evaluateContext(contextId: String) {
        contexts[contextId]?.let {
            notifyBindingChanges(it)
        }
    }

    private fun getValue(contextData: ContextData, path: String): Any? {
        return if (path != contextData.id) {
            val value = lruCache[path]
            if (value != null) {
                return value
            }
            findAndCacheValue(contextData, path)
        } else {
            contextData.value
        }
    }

    private fun findAndCacheValue(contextData: ContextData, path: String): Any? {
        return try {
            val keys = contextPathResolver.getKeysFromPath(contextData.id, path)
            val foundValue = jsonPathFinder.find(keys, contextData.value)
            if (foundValue != null) {
                lruCache.put(path, foundValue)
            }
            foundValue
        } catch (ex: Exception) {
            BeagleMessageLogs.errorWhileTryingToAccessContext(ex)
            null
        }
    }

    private fun setValue(contextBinding: ContextBinding, path: String, value: Any): Boolean {
        val context = contextBinding.context
        return if (path == context.id) {
            val newContext = context.copy(value = value)
            contexts[context.id] = contextBinding.copy(context = newContext)
            true
        } else {
            return try {
                val keys = contextPathResolver.getKeysFromPath(context.id, path)
                jsonPathReplacer.replace(keys, value, context.value)
            } catch (ex: Exception) {
                BeagleMessageLogs.errorWhileTryingToChangeContext(ex)
                false
            }
        }
    }

    private fun notifyBindingChanges(contextBinding: ContextBinding) {
        val contextData = contextBinding.context
        val bindings = contextBinding.bindings

        bindings.forEach { bind ->
            val value = evaluateBinding(contextData, bind)
            if (value != null) {
                bind.notifyChange(value)
            }
        }
    }

    private fun Bind.Expression<*>.getContextId(): String =
        valueInExpression().split(".")[0]

    private fun Bind.Expression<*>.valueInExpression(): String =
        EXPRESSION_REGEX.find(this.value)?.groups?.get(1)?.value ?: ""
}