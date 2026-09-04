package com.app.koshpal.core.sms.dedup

import com.app.koshpal.core.sms.model.ParsedTransaction

interface DuplicateDetector {
    fun removeDuplicates(existing: List<ParsedTransaction>, incoming: List<ParsedTransaction> ): List<ParsedTransaction>
}