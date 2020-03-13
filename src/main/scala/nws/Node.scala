package nws

import java.net.{InetSocketAddress, SocketAddress}
import java.nio.charset.Charset

import org.apache.mina.core.session.{IdleStatus, IoSession}
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.apache.mina.transport.socket.nio.NioSocketConnector

import scala.io.StdIn

class Node {

  val codecFactory = new PrefixedStringCodecFactory(Charset.forName("UTF-8"))

  def initAcceptor(port: Int)(replyHandler: PartialFunction[String,String])(implicit config: Config): NioSocketAcceptor = {
    val acceptor = new NioSocketAcceptor
    acceptor.getFilterChain.addLast("codec", new ProtocolCodecFilter(codecFactory))
    acceptor.setHandler(NodeHandler(replyHandler))
    acceptor.getSessionConfig.setReadBufferSize(config.readBufferSize)
    acceptor.getSessionConfig.setIdleTime(IdleStatus.READER_IDLE,config.readerIdleTime)
    acceptor.getSessionConfig.setIdleTime(IdleStatus.WRITER_IDLE,config.writerIdleTime)
    acceptor.bind(new InetSocketAddress(port))
    acceptor
  }

  def connect(host: String, port: Int)(implicit config: Config): IoSession = {
    val remoteAddress = new InetSocketAddress(host, port)
    val connector = new NioSocketConnector
    connector.getFilterChain.addLast("codec", new ProtocolCodecFilter(codecFactory))
    connector.getSessionConfig.setSendBufferSize(config.sendBufferSize)
    connector.getSessionConfig.setIdleTime(IdleStatus.READER_IDLE,config.readerIdleTime)
    connector.getSessionConfig.setIdleTime(IdleStatus.WRITER_IDLE,config.writerIdleTime)
    val connection = connector.connect(remoteAddress)
    connection.await.getSession
  }
}
