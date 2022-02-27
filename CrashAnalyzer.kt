package com.amvac.amvacrfid.utils.logfile

import com.amvac.amvacrfid.utils.AmvacAppConstants


/**
 * This class used for creating log string of exception caused, class name and number
 * @throwable throwable of exception
 */
class CrashAnalyzer(private val throwable: Throwable? = null) {

    fun getAnalysis(): String {
        val factsBuilder = StringBuilder()
        val placeOfCrash: String

        factsBuilder.append(stackTrace(throwable?.stackTrace!!))
        factsBuilder.append("\n")
        if (throwable.cause != null) {
            factsBuilder.append(AmvacAppConstants.EXCEPTION_LOG_CAUSED_BY)
            val stackTrace = throwable.cause?.stackTrace
            placeOfCrash = getCrashOriginatingClass(stackTrace)
            factsBuilder.append(stackTrace(stackTrace!!))
        } else {
            placeOfCrash = getCrashOriginatingClass(throwable.stackTrace)
        }

        factsBuilder.append("\n")
        factsBuilder.append(placeOfCrash)
        factsBuilder.append("\n")
        factsBuilder.append(AmvacAppConstants.LINE_NUMBER)
        factsBuilder.append(throwable.stackTrace[0].lineNumber.toString())
        factsBuilder.append("\n")
        factsBuilder.append(AmvacAppConstants.EXCEPTION_MESSAGE)
        factsBuilder.append("\n")
        factsBuilder.append(throwable.javaClass.canonicalName)

        return factsBuilder.toString()
    }

    private fun stackTrace(stackTrace: Array<out StackTraceElement>): String {
        val builder = StringBuilder()
        for (stackTraceElement in stackTrace) {
            builder.append(AmvacAppConstants.EXCEPTION_LOG_AT)
            builder.append(stackTraceElement.toString())
            builder.append("\n")
        }
        return builder.toString()
    }

    private fun getCrashOriginatingClass(stackTraceElements: Array<StackTraceElement>?): String {
        if (stackTraceElements != null && stackTraceElements.isNotEmpty()) {
            val stackTraceElement = stackTraceElements[0]
            return String.format(AmvacAppConstants.EXCEPTION_CLASS_AND_LINE_NUMBER_FORMAT,
                stackTraceElement.className,
                stackTraceElement.lineNumber)
        }
        return AmvacAppConstants.EMPTY_STRING
    }
}