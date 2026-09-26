package com.example.dictionary.engine

object FuzzySearchEngine {

    /**
     * Calculates the Levenshtein distance between two strings.
     */
    fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) {
            dp[i][0] = i
        }
        for (j in 0..s2.length) {
            dp[0][j] = j
        }

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    /**
     * Checks if target is a fuzzy match for query with appropriate tolerance.
     */
    fun isFuzzyMatch(query: String, target: String): Boolean {
        if (query.isEmpty() || target.isEmpty()) return false
        val nQuery = PersianTextNormalizer.normalize(query)
        val nTarget = PersianTextNormalizer.normalize(target)

        if (nTarget.contains(nQuery) || nQuery.contains(nTarget)) return true

        val maxAllowedDistance = when {
            nQuery.length <= 3 -> 1
            nQuery.length <= 6 -> 2
            else -> 3
        }

        val distance = levenshteinDistance(nQuery, nTarget)
        return distance <= maxAllowedDistance
    }
}
