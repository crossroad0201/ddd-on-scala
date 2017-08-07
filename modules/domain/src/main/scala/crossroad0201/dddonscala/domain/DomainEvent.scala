package crossroad0201.dddonscala.domain

import scala.util.Try

trait DomainEvent

trait EventPublisher {
  type Format

  def publish[EVENT <: DomainEvent, Format](event: EVENT)(implicit marshaller: EventMarshaller[EVENT, Format]): Try[EVENT]
}

trait EventMarshaller[EVENT <: DomainEvent, FORMAT] {
  def marshal(event: EVENT): Try[FORMAT]
}
