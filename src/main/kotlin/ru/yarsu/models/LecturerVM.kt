package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.Course

class LecturerVM(val courses: List<Course>):ViewModel {
}