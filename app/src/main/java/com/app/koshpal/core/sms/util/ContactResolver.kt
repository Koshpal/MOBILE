package com.app.koshpal.core.sms.util

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import timber.log.Timber
import java.util.Locale

class ContactResolver(private val context: Context) {

    fun resolveContactName(partyName: String): String? {
        val phoneNumber = extractPhoneNumber(partyName)
        if (phoneNumber != null) {
            val name = getNameFromContactsByNumber(phoneNumber)
            if (name != null) return name
        }

        return resolveByName(partyName)
    }

    private fun extractPhoneNumber(text: String): String? {
        val upiMatch = Regex("""(\d{10,12})@""").find(text)
        if (upiMatch != null) return normalizeNumber(upiMatch.groupValues[1])

        val rawMatch = Regex("""(\d{10,12})""").find(text)
        if (rawMatch != null) return normalizeNumber(rawMatch.groupValues[1])

        return null
    }

    private fun normalizeNumber(number: String): String {
        return if (number.length > 10) number.takeLast(10) else number
    }

    private fun getNameFromContactsByNumber(phoneNumber: String): String? {
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) return cursor.getString(0)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error querying contacts for number $phoneNumber")
        }
        return null
    }

    private fun resolveByName(partyName: String): String? {
        val keywords = partyName.lowercase(Locale.ROOT)
            .split(Regex("""[\s\-/.]+"""))
            .filter { it.isNotBlank() && it.length >= 2 }
        
        if (keywords.isEmpty()) return null

        try {
            val uri = ContactsContract.Contacts.CONTENT_URI
            val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            
            for (keyword in keywords) {
                val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
                val selectionArgs = arrayOf("%$keyword%")
                
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val displayName = cursor.getString(0)
                        if (isFuzzyMatch(partyName, displayName)) {
                            return displayName
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error querying contacts by name for $partyName")
        }
        return null
    }

    private fun isFuzzyMatch(partyName: String, contactName: String): Boolean {
        val mLower = partyName.lowercase(Locale.ROOT)
        val cLower = contactName.lowercase(Locale.ROOT)
        
        val delimiters = Regex("""[\s\-.]+""")
        val receiverWords = mLower.split(delimiters).filter { it.isNotBlank() }
        
        val noise = listOf("master", "mr", "ms", "mrs", "dr", "via", "upi", "to", "paid")
        val contactWords = cLower.split(delimiters).filter { it.isNotBlank() && it !in noise }

        if (contactWords.isEmpty()) return false

        var score = 0
        
        val first = contactWords.first()
        val last = if (contactWords.size > 1) contactWords.last() else null

        if (receiverWords.any { it == first }) {
            score += 60
        }
        
        if (last != null && receiverWords.any { it == last }) {
            score += 40
        }

        contactWords.forEachIndexed { index, word ->
            if (index != 0 && (last == null || index != contactWords.lastIndex)) {
                if (receiverWords.any { it == word || (word.length == 1 && it.startsWith(word)) || (it.length == 1 && word.startsWith(it)) }) {
                    score += 15
                }
            }
        }

        return score >= 70
    }
}
