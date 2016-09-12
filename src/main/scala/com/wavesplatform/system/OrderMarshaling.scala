package com.wavesplatform.system

import com.wavesplatform.matcher.{Instrument, Order}
import spray.json._
import spray.json.DefaultJsonProtocol

case class TicketRequest(tickets: Int) {
  require(tickets > 0)
}


trait OrderMarshalling  extends DefaultJsonProtocol {

  implicit val instrumentFormat = jsonFormat1(Instrument)
  //implicit val orderFormat = jsonFormat5(Order)
  implicit val ticketRequestFormat = jsonFormat1(TicketRequest)
}