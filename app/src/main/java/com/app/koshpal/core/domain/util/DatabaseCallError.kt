package com.app.koshpal.core.domain.util

enum class DatabaseCallError : CallError {
    UNIQUE_CONSTRAINT_VIOLATION,
    FOREIGN_KEY_VIOLATION,
    DISK_FULL,
    READ_ERROR,
    WRITE_ERROR,
    NOT_FOUND,
    UNKNOWN
}