package dev.djakonystar.antisihr.ui.feedback.validator

import dev.djakonystar.antisihr.data.models.drawerlayout.AddFeedbackData
import dev.djakonystar.antisihr.ui.feedback.FeedbackScreen

class FeedbackValidator(private val feedback: AddFeedbackData) {

    fun isValid(): Boolean {
        return feedback.fio.isEmpty() && feedback.phone.isNotEmpty() && feedback.title.isNotEmpty() && feedback.description.isNotEmpty()
    }


    fun isNotValidName() = feedback.fio.isEmpty()

    fun isNotValidPhone() = feedback.phone.isEmpty()
    fun isNotValidTitle() = feedback.title.isEmpty()
    fun isNotValidDescription() = feedback.description.isEmpty()
}