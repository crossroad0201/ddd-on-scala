package crossroad0201.dddonscala.domain

import crossroad0201.dddonscala.domain.user.User

package object task {
  import scala.language.implicitConversions

  case class TaskId(value: String) extends AnyVal with EntityId
  object TaskId {
    def newId(implicit idGen: EntityIdGenerator): TaskId =
      TaskId(idGen.genId())
  }

  /*
   * NOTE: ドメインモデルで扱う値はすべて値型を定義し、Stringなどのプリミティブ型では扱わないようにします。
   *
   * タイプエイリアスを使うことで、プリミティブ型にドメインの名前を付けることもできますが、
   * あくまでも別名が付くだけで形安全にはなりません。
   */

  /*
   * NOTE: 値型は scala.AnyVal を継承することで、実行時の値型の生成コストを抑えます。
   * http://docs.scala-lang.org/ja/overviews/core/value-classes.html
   */

  case class TaskName(value: String) extends AnyVal with Value[String]

  case class CommentMessage(value: String) extends AnyVal with Value[String]

  sealed abstract class TaskState(val value: String)
  object TaskState {
    case object Opened extends TaskState("OPENED")
    case object Closed extends TaskState("CLOSED")
    val values: Set[TaskState] = Set(Opened, Closed)

    def valueOf(value: String): TaskState =
      values.find(_.value == value).getOrElse { throw new IllegalArgumentException(s"$value は未定義の値です。") }
  }

  /*
   * NOTE: 他の集約との関連は、その「ロール」をモデルとして定義し、そのロール型にドメインの振る舞いを定義します。
   * （ここでは、「作成者」「担当者」「コメント記入者」と言うロールをモデル化しています）
   *
   * こうすることで、関連先のエンティティに変更を加えることなく、自集約での振る舞いを自然な形で定義できます。
   */
  // NOTE: ロール型は implicit class とすることで、エンティティを暗黙的にロール型に変換できます。

  implicit class Author(user: User) {
    // NOTE: 「他の集約から生み出されるエンティティ」を生成するファクトリは、ロール型を定義します。
    def createTask(name:                            TaskName)(implicit idGen: EntityIdGenerator,
                                   metaDataCreator: EntityMetaDataCreator): DomainResult[Task, TaskCreated] = {
      val task = Task(
        id       = TaskId.newId,
        name     = name,
        authorId = user.id,
        metaData = metaDataCreator.create
      )
      val event = TaskCreated(
        taskId   = task.id,
        name     = task.name,
        authorId = task.authorId
      )
      DomainResult(task, event)
    }
  }

  implicit class Assignee(user: User) {
    def assignTo(task: Task): Either[TaskAlreadyClosed, DomainResult[Task, TaskAssigned]] =
      task.assign(user)
  }

  implicit class Commenter(user: User) {
    def commentTo(task: Task, message: CommentMessage): DomainResult[Task, TaskCommented] =
      task.addComment(Comment(message, user.id))
  }
}
