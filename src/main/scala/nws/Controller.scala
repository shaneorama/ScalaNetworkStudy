package nws

import scala.io.StdIn

object Controller extends App {
  implicit val config: Config = Config.load()

  val controlNode = new Node

  val session = controlNode.connect("localhost", config.controlPort)
  session.write("Are you the control node?")

  controlNode.initAcceptor(config.controlPort){
    case "Are you the control node?" => "Why yes I am, how can I help you?"
  }



}

//    if(args.length == 1 && args(0).matches("[0-9]+")) {
//      val port = args(0).toInt
//    } else if(args.length == 2 && args(1).matches("[0-9]+")) {
//      val host = args(0)
//      val port = args(1).toInt
//      val session = connect(host, port)
//
//      while(true) {
//        val input = StdIn.readLine(":> ")
//        session.write(input)
//      }
//
//    } else {
//      println
//      """
//        |Usage: Node <port> | <host> <port>
//        |""".stripMargin
//    }
//}
