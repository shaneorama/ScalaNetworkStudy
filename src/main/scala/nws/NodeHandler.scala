package nws

import org.apache.mina.core.service.IoHandlerAdapter
import org.apache.mina.core.session.{IdleStatus, IoSession}

case class NodeHandler(replyHandler: PartialFunction[String,String]) extends IoHandlerAdapter {
  override def exceptionCaught(session: IoSession, cause: Throwable): Unit = {
    cause.printStackTrace()
  }

  override def messageReceived(session: IoSession, message: Any): Unit = {
    replyHandler.lift.apply(message.toString) match {
      case Some(reply) => session.write(reply)
      case _ => session.closeNow()
    }
  }

  override def sessionIdle(session: IoSession, status: IdleStatus): Unit = {
    println(s"IDLE ${session.getIdleCount(status)}")
  }
}
