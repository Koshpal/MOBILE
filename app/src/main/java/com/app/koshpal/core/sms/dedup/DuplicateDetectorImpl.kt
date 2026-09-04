package com.app.koshpal.core.sms.dedup

import com.app.koshpal.core.sms.model.ParsedTransaction
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DuplicateDetectorImpl : DuplicateDetector {

    override fun removeDuplicates(
        existing: List<ParsedTransaction>,
        incoming: List<ParsedTransaction>,
    ): List<ParsedTransaction> {
        val existingMap = existing.associateBy { fingerprint(it) }
        val incomingMap = mutableMapOf<String, ParsedTransaction>()

        for (txn in incoming) {
            val key = fingerprint(txn)
            
            val inDb = existingMap[key]
            if (inDb != null) {
                // Cross-message confidence merge: only emit if incoming provides a better identity
                val merged = merge(inDb, txn)
                if (merged.senderName != inDb.senderName || merged.receiverName != inDb.receiverName) {
                    val currentIncoming = incomingMap[key]
                    incomingMap[key] = if (currentIncoming != null) merge(currentIncoming, merged) else merged
                } else {
                    Timber.d("Duplicate blocked (identical or lower confidence than DB): $key")
                }
                continue
            }

            val existingIncoming = incomingMap[key]
            if (existingIncoming != null) {
                incomingMap[key] = merge(existingIncoming, txn)
            } else {
                incomingMap[key] = txn
            }
        }

        return incomingMap.values.toList()
    }

    private fun merge(t1: ParsedTransaction, t2: ParsedTransaction): ParsedTransaction {
        val (finalSender, finalVpa) = betterSender(t1, t2)
        return t1.copy(
            senderName = finalSender ?: "",
            isVpaAnchored = finalVpa,
            receiverName = betterName(t1.receiverName, t2.receiverName) ?: "",
            mode = t1.mode ?: t2.mode,
            referenceNumber = t1.referenceNumber ?: t2.referenceNumber,
            accountNumber = t1.accountNumber ?: t2.accountNumber,
        )
    }

    private fun betterSender(t1: ParsedTransaction, t2: ParsedTransaction): Pair<String?, Boolean> {
        if (t1.isVpaAnchored && !t2.isVpaAnchored) return t1.senderName to true
        if (t2.isVpaAnchored && !t1.isVpaAnchored) return t2.senderName to true
        return betterName(t1.senderName, t2.senderName) to (t1.isVpaAnchored)
    }

    private fun betterName(n1: String?, n2: String?): String? {
        if (n1.isNullOrBlank()) return n2
        if (n2.isNullOrBlank()) return n1
        
        val s1 = n1.lowercase().trim()
        val s2 = n2.lowercase().trim()
        val generic = setOf("me", "my", "self", "unknown")
        
        if (s1 in generic && s2 !in generic) return n2
        if (s2 in generic && s1 !in generic) return n1
        return n1
    }

    private fun fingerprint(t: ParsedTransaction): String {
        if (!t.referenceNumber.isNullOrBlank()) {
            return "ref:${t.bank}:${t.referenceNumber.trim()}"
        }
        
        val timeBucket = t.timestamp / TIME_BUCKET_MS 
        val normalizedAccount = t.accountNumber?.takeLast(4) ?: "0"
        return "comp:${t.bank}:${t.type}:${t.amount}:$normalizedAccount:$timeBucket"
    }

    companion object {
        private val TIME_BUCKET_MS = TimeUnit.MINUTES.toMillis(2)
    }
}
