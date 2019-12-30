package logView

import java.util.regex.Pattern

class FileName2Date2(fileName:String) {
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var isValid: Boolean = false

    init {
        val pattern = Pattern.compile("^(\\d+)-(\\d+)-(\\d+)")
        val matcher = pattern.matcher(fileName)
        if (!matcher.find()) {
            this.isValid = false
        } else {
            this.isValid = true

            year = matcher.group(1).toInt()
            month= matcher.group(2).toInt()
            day = matcher.group(3).toInt()
        }
    }

    fun isValid(): Boolean {
        return this.isValid
    }

    fun getYear(): Int {
        return this.year
    }

    fun getMonth(): Int {
        return this.month
    }

    fun getDay(): Int {
        return this.day
    }
}