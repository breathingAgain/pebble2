package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.Course

class CourseListVM(val courses: List<Course>):ViewModel {
}