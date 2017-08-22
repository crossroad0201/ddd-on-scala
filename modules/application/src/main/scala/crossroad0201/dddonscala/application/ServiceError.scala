package crossroad0201.dddonscala.application

import crossroad0201.dddonscala.domain.EntityId
import crossroad0201.dddonscala.infrastructure.rdb.OptimisticLockException

abstract sealed class ServiceError(val errorCode: ErrorCode, val args: Any*) {
  protected val stackTrace = {
    val traces = Thread.currentThread().getStackTrace
    traces.drop(traces.lastIndexWhere(t => t.getFileName.contains("ApplicationError.scala")) + 1)
  }

  override def toString = {
    s"""${getClass.getName}($errorCode, [${args.mkString(", ")}])
      |${stackTrace.map(s => s"  at $s").mkString("\n")}
    """.stripMargin
  }
}

case class SystemError(cause: Throwable) extends ServiceError("error.system")

abstract class ApplicationError(errorCode: ErrorCode, args: Any*) extends ServiceError(errorCode, args: _*)

case class NotFoundError(entityType: String, id: EntityId) extends ApplicationError("error.notFound", entityType, id)

case class ConflictError(cause: OptimisticLockException)
    extends ApplicationError("error.conflict", cause.id, cause.version)
