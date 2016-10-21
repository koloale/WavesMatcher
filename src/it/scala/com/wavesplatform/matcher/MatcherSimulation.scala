package com.wavesplatform.matcher

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class MatcherSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:9000") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers

  val feeder = csv("orders.csv").random

  val headersPost = Map("Content-Type" -> "application/x-www-form-urlencoded") // Note the headers specific to a given request

  val scn = scenario("Place orders in matcher")
    .feed(feeder)
    .exec(http("Place order")
      .post("/matcher/orders/place")
      .body(StringBody("""{
                         |  "price": ${price},
                         |  "spendAssetId": "${spendAssetId}",
                         |  "signature": "signature",
                         |  "amount": ${amount},
                         |  "matcher": "matcher",
                         |  "receiveAssetId": "${receiveAssetId}",
                         |  "sender": "sender123"
                         |}""".stripMargin)).asJSON
      .check(status.is(200))
      .check(jsonPath("$..id").ofType[String] )
    )
    .pause(1)
    /*.exec(http("Create a new comp")
      .post("/computers")
      .headers(headersPost)
      .formParam("name", "MyAlexComp") // Note the triple double quotes: used in Scala for protecting a whole chain of characters (no need for backslash)
      .formParam("introduced", "2012-05-30")
      .formParam("discontinued", "")
      .formParam("company", "37"))*/


  setUp(scn.inject(constantUsersPerSec(1000) during(10 seconds)).throttle(
    reachRps(1000) in (10 seconds),
    holdFor(1 minute)
  ).protocols(httpConf))

  //setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
}
