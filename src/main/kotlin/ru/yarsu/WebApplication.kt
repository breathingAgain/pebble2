package ru.yarsu

import org.http4k.core.*
import ru.yarsu.models.PebbleViewModel
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.template.PebbleTemplates
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import ru.yarsu.domain.Course
import ru.yarsu.domain.ScheduleInfo
import ru.yarsu.domain.formSchedule
import ru.yarsu.models.CourseListVM
import ru.yarsu.models.LecturerVM
import ru.yarsu.models.StartPageVM

private fun showPebbleTemplate(bodyLens: BiDiBodyLens<ViewModel>,): HttpHandler = {
    val viewModel = PebbleViewModel("Hello there!")
    Response(OK).with(bodyLens of viewModel)
}

private fun respondWithPong(): HttpHandler = {
    Response(OK).body("pong")
}

fun router(
    scheduleInfo: ScheduleInfo,
    bodyLens: BiDiBodyLens<ViewModel>,
): HttpHandler = routes(
    "/ping" bind GET to respondWithPong(),
    "/templates/pebble" bind GET to showPebbleTemplate(bodyLens),
    "/" bind GET to showStartPage(bodyLens),
    "/lecturers" bind GET to showLecturersList(bodyLens,scheduleInfo),
    "/lecturer/{name}" bind GET to showLecturer(bodyLens,scheduleInfo),
    static(ResourceLoader.Classpath("/ru/yarsu/public/")),

)

fun main() {
    val scheduleInfo = formSchedule()
    val renderer = Body.viewModel(
        PebbleTemplates().HotReload("src/main/resources"),
        ContentType.TEXT_HTML,
    ).toLens()
    val router = router(scheduleInfo, renderer)

    val app: HttpHandler = Filter.NoOp.then(router)
    val server = app.asServer(Netty(9000)).start()
    println("Server started on http://localhost:" + server.port())
}
fun showStartPage(lens:BiDiBodyLens<ViewModel>): HttpHandler ={
val start = StartPageVM()
Response(OK).with(lens of start)
}
fun showLecturersList(lens: BiDiBodyLens<ViewModel>, scheduleInfo: ScheduleInfo): HttpHandler = {
    val courses = CourseListVM(scheduleInfo.courses)
    Response(OK).with(lens of courses)
}
fun showLecturer(lens: BiDiBodyLens<ViewModel>, scheduleInfo: ScheduleInfo): HttpHandler = {
    reqest-> val name = reqest.path("name")
    val courses = ArrayList<Course>()
    for(course in scheduleInfo.courses)
    {
        if (course.lecturerFullName==name)
            courses.add(course)
    }
    val subjectsModel = LecturerVM(courses)
    Response(OK).with(lens of subjectsModel)
}

