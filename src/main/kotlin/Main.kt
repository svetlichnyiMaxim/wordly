import GameSolver.processWordCheck

const val SIZE = 3
val allowedChars = ('a'..'z').toList()

object Position {
    private var word = ""

    init {
        for (i in 0 until SIZE) {
            word += allowedChars.random()
        }
    }

    fun display() {
        println(word)
    }

    fun check(wordToCheck: CharArray): Pair<MutableList<Int>, MutableList<Int>> {
        require(wordToCheck.size == SIZE)
        val foundLetter1: MutableList<Int> = mutableListOf()
        val possibleLetter1: MutableList<Int> = mutableListOf()
        val wordCopy = word.toCharArray()
        for (i in 0 until SIZE) {
            if (wordCopy[i] == wordToCheck[i]) {
                foundLetter1.add(i)
                wordCopy[i] = ' '
            }
        }
        for (i in 0 until SIZE) {
            for (i2 in 0 until SIZE) {
                if (wordCopy[i2] == wordToCheck[i]) {
                    possibleLetter1.add(i)
                    wordCopy[i2] = ' '
                }
            }
        }
        return Pair(foundLetter1, possibleLetter1)
    }
}

class Char1(val possiblePositions: MutableSet<Int>, val foundPositions: MutableSet<Int>)

class WinException(solution: CharArray) :
    Exception("You have already won (it is ${solution.joinToString { it.toString() }})")

/**
 * it tries to find an optimal solution
 */
object GameSolver {
    private val chars: MutableMap<Char, Char1> = mutableMapOf()

    init {
        allowedChars.forEach {
            chars[it] = Char1(mutableSetOf(), mutableSetOf())
            for (i in 0 until SIZE)
                chars[it]!!.possiblePositions.add(i)
        }
    }

    fun getWord(): CharArray {
        val word = CharArray(SIZE) { ' ' }
        val possiblePos = chars.filter { it.value.possiblePositions.isNotEmpty() }

        if (chars.toList().sumOf { it.second.foundPositions.size } >= SIZE) {
            val solution = CharArray(SIZE) { ' ' }
            solution.forEachIndexed { index, _ ->
                solution[index] = (chars.toList().find { it.second.foundPositions.contains(index) }!!).first
            }
            throw WinException(solution)
        }
        while (word.contains(' ')) {
            possiblePos.forEach { (t, u) ->
                word.forEachIndexed { index, c ->
                    // is we can use it
                    if (u.possiblePositions.contains(index) && c == ' ' && !u.foundPositions.contains(index)) {
                        word[index] = t
                        return@forEach
                    }
                }
            }
        }
        return word
    }

    fun processWordCheck(wordToCheck: CharArray) {
        val checkResult = Position.check(wordToCheck)
        // process found letters
        checkResult.first.forEach {
            chars[wordToCheck[it]]!!.possiblePositions.remove(it)
            chars[wordToCheck[it]]!!.foundPositions.add(it)
        }
        val correctOnes = checkResult.second.toMutableList().apply { this.addAll(checkResult.first) }
        for (i in 0 until SIZE) {
            if (!correctOnes.contains(i)) {
                chars[wordToCheck[i]]!!.possiblePositions.clear()
            } else {
                chars[wordToCheck[i]]!!.possiblePositions.remove(i)
            }
        }
    }
}

fun main() {
    Position.display()
    while (true) {
        println("best word is ${GameSolver.getWord().joinToString { it.toString() }}")
        println()
        processWordCheck(GameSolver.getWord())
        Position.check(GameSolver.getWord()).let { result ->
            println("correct ones")
            println(result.first.joinToString { it.toString() })
            println()
            println("semi-correct")
            println(result.second.joinToString { it.toString() })
            println()
        }
    }
}
