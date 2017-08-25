package crossroad0201.dddonscala.application

import crossroad0201.dddonscala.domain.EntityId

abstract sealed class ServiceError(val errorCode: ErrorCode, val args: Any*) {
  /*
   * 独自に作成したエラーがどこで発生したのかを追跡しやすくするために、
   * 例外と同様にスタックトレースを持たせるようにしています。
   */
  protected val stackTrace = {
    val traces = Thread.currentThread().getStackTrace
    traces.drop(traces.lastIndexWhere(t => t.getClassName == getClass.getName) + 1)
  }

  override def toString = {
    s"""${getClass.getName}($errorCode, [${args.mkString(", ")}])
      |${stackTrace.map(s => s"  at $s").mkString("\n")}
    """.stripMargin
  }
}

case class SystemError(cause: Throwable) extends ServiceError(ServiceErrorCodes.SystemError)

abstract class ApplicationError(errorCode: ErrorCode, args: Any*) extends ServiceError(errorCode, args: _*)

case class NotFoundError(entityType: String, id: EntityId)
    extends ApplicationError(ServiceErrorCodes.NotFound, entityType, id)

case class ConflictedError(id: EntityId) extends ApplicationError(ServiceErrorCodes.Conflicted, id)

object ServiceErrorCodes {
  val SystemError = "error.system"
  val NotFound    = "error.notFound"
  val Conflicted  = "error.conflicted"

  // in TaskService
  val InvalidTaskOperation = "error.invalidTaskOperation"

}
