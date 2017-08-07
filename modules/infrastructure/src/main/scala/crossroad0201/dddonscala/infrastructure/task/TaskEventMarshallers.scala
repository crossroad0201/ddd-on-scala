package crossroad0201.dddonscala.infrastructure.task

import crossroad0201.dddonscala.domain.EventMarshaller
import crossroad0201.dddonscala.domain.task.{ TaskAssigned, TaskCreated }
import crossroad0201.dddonscala.infrastructure.JSON

import scala.util.Success

object TaskEventMarshallers {
  implicit val taskCreatedMarshaller: EventMarshaller[TaskCreated, JSON] = (event: TaskCreated) => {
    Success(
      s"""
         |{
         |  "task_id": "${event.taskId.value}",
         |  "name": "${event.name}"
         |}
      """.stripMargin
    )
  }

  implicit val taskAssignedMarshaller: EventMarshaller[TaskAssigned, JSON] = (event: TaskAssigned) => {
    Success(
      s"""
         |{
         |  "task_id": "${event.taskId.value}",
         |  "assignee_id": "${event.assigneeId.value}"
         |}
      """.stripMargin
    )
  }
}
