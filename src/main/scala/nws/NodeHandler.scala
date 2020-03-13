package nws

import java.util.Date

import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.{IdleStatus, IoSession}

class NodeHandler extends IoHandlerAdapter {
  override def exceptionCaught(session: IoSession, cause: Throwable): Unit = {
    cause.printStackTrace()
  }

  override def messageReceived(session: IoSession, message: Any): Unit = {
    message.toString.trim match {
      case "quit" => session.closeNow()
      case _ =>
        println(message.toString)
        session.write(message.toString)
    }
  }

  override def sessionIdle(session: IoSession, status: IdleStatus): Unit = {
    println(s"IDLE ${session.getIdleCount(status)}")
  }


}
