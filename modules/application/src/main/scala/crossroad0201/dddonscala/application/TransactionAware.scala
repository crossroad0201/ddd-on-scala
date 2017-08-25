package crossroad0201.dddonscala.application

import crossroad0201.dddonscala.domain.UnitOfWork

trait TransactionAware {

  def tx[A](f: UnitOfWork => A): A

  def txReadonly[A](f: UnitOfWork => A): A

}
