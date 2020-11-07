package com.example.tetris.helpers

fun array2dOfByte(n: Int, m: Int): Array<ByteArray> {
    val result =  Array(n){ByteArray(m)}
    for (i in 0 until n) {
        for (j in 0 until m) {
            result[i][j] = 0
        }
    }
    return result
}