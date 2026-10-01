package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ResourcesTest {

    @Test
    fun defaultAndEnglishResourcesDeclareTheSameKeys() {
        val defaultKeys = parseStrings(defaultStrings()).keys
        val englishKeys = parseStrings(englishStrings()).keys

        assertTrue("default resource has no keys", defaultKeys.isNotEmpty())
        assertTrue(
            "keys present in values but missing from values-en: ${defaultKeys - englishKeys}",
            englishKeys.containsAll(defaultKeys)
        )
        assertTrue(
            "keys present in values-en but missing from values: ${englishKeys - defaultKeys}",
            defaultKeys.containsAll(englishKeys)
        )
        assertEquals(547, defaultKeys.size)
        assertEquals(547, englishKeys.size)
    }

    @Test
    fun noStringValueIsEmpty() {
        for ((name, file) in listOf("values" to defaultStrings(), "values-en" to englishStrings())) {
            val entries = parseStrings(file)
            entries.forEach { (key, value) ->
                if (value.isBlank()) fail("$name/$key has a blank value")
            }
            assertEquals("$name must not contain duplicates", entries.size, countStringTags(file))
        }
    }

    @Test
    fun formatArgumentsMatchAcrossLocales() {
        val defaultEntries = parseStrings(defaultStrings())
        val englishEntries = parseStrings(englishStrings())

        val mismatches = mutableListOf<String>()
        for ((key, defaultValue) in defaultEntries) {
            val englishValue = englishEntries[key] ?: continue
            val defaultTokens = formatTokens(defaultValue)
            val englishTokens = formatTokens(englishValue)
            if (defaultTokens != englishTokens) {
                mismatches += "$key: values=$defaultTokens values-en=$englishTokens"
            }
        }

        if (mismatches.isNotEmpty()) fail("format argument mismatches:\n${mismatches.joinToString("\n")}")
        assertTrue("at least one formatted string must exist", defaultEntries.values.any { formatTokens(it).isNotEmpty() })
    }

    @Test
    fun positionalFormatSpecifiersAreWellFormed() {
        val entries = parseStrings(defaultStrings()) + parseStrings(englishStrings())
        entries.forEach { (key, value) ->
            val tokens = rawFormatTokens(value)
            tokens.forEach { token ->
                if (!token.matches(Regex("""%\d+\$[.\d]*[a-zA-Z]""")) && token != "%") {
                    fail("$key contains a non-positional format specifier '$token'")
                }
            }
        }
    }

    // ---- helpers -------------------------------------------------------------

    private fun parseStrings(file: File): Map<String, String> {
        assertTrue("missing resource file $file", file.isFile)
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val nodes = document.getElementsByTagName("string")
        val entries = LinkedHashMap<String, String>()
        for (i in 0 until nodes.length) {
            val element = nodes.item(i) as Element
            val key = element.getAttribute("name")
            val previous = entries.put(key, element.textContent)
            if (previous != null) fail("duplicate string key '$key' in $file")
        }
        return entries
    }

    private fun countStringTags(file: File): Int =
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            .getElementsByTagName("string").length

    /** Tokens such as `%1$s` after `%%` escapes have been removed. */
    private fun formatTokens(value: String): List<String> =
        rawFormatTokens(value.replace("%%", "\u0000")).sorted()

    /** Returns the raw `%…` specifiers of [value], ignoring `%%` escapes. */
    private fun rawFormatTokens(value: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < value.length) {
            if (value[i] != '%') {
                i++
                continue
            }
            if (i + 1 < value.length && (value[i + 1] == '%' || value[i + 1] == '\u0000')) {
                i += 2
                continue
            }
            var j = i + 1
            while (j < value.length && value[j].isDigit()) j++
            if (j < value.length && value[j] == '$') j++
            while (j < value.length && (value[j].isDigit() || value[j] == '.')) j++
            if (j < value.length && value[j].isLetter()) j++
            tokens += value.substring(i, j)
            i = j
        }
        return tokens
    }

    private fun findProjectFile(relativePath: String): File {
        var directory = File(System.getProperty("user.dir") ?: ".").absoluteFile
        repeat(10) {
            val candidate = File(directory, relativePath)
            if (candidate.isFile) return candidate
            val parent = directory.parentFile ?: return@repeat
            directory = parent
        }
        fail("could not locate $relativePath starting from ${System.getProperty("user.dir")}")
        throw IllegalStateException("unreachable")
    }

    private fun defaultStrings(): File = findProjectFile("app/src/main/res/values/strings.xml")
    private fun englishStrings(): File = findProjectFile("app/src/main/res/values-en/strings.xml")
}
