package com.wavesplatform.system

import spray.json._
import spray.json.DefaultJsonProtocol

case class TicketRequest(tickets: Int) {
  require(tickets > 0)
}


trait OrderMarshalling  extends DefaultJsonProtocol {

  import com.wavesplatform.matcher.Orders._

  implicit val buyFormat = jsonFormat4(Buy)
  implicit val sellFormat = jsonFormat4(Sell)
  implicit val ticketRequestFormat = jsonFormat1(TicketRequest)
}