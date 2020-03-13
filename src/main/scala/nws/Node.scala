package nws

import java.net.{InetSocketAddress, SocketAddress}
import java.nio.charset.Charset

import org.apache.mina.core.session.{IdleStatus, IoSession}
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.apache.mina.transport.socket.nio.NioSocketConnector

import scala.io.StdIn

object Node {

  val codecFactory = new PrefixedStringCodecFactory(Charset.forName("UTF-8"))

  def main(args: Array[String]): Unit = {
    implicit val config: Config = Config.load()

    if(args.length == 1 && args(0).matches("[0-9]+")) {
      val port = args(0).toInt
      initAcceptor(port)
    } else if(args.length == 2 && args(1).matches("[0-9]+")) {
      val host = args(0)
      val port = args(1).toInt
      val session = connect(host, port)

      while(true) {
        val input = StdIn.readLine(":> ")
        session.write(input)
      }

    } else {
      println
      """
        |Usage: Node <port> | <host> <port>
        |""".stripMargin
    }
  }


  def connect(host: String, port: Int)(implicit config: Config): IoSession = {
    val remoteAddress = new InetSocketAddress(host, port)
    val connector = new NioSocketConnector
    connector.getFilterChain.addLast("codec", new ProtocolCodecFilter(codecFactory))

    connector.setHandler(new NodeHandler)

    connector.getSessionConfig.setSendBufferSize(config.sendBufferSize)
    connector.getSessionConfig.setIdleTime(IdleStatus.READER_IDLE,config.readerIdleTime)
    connector.getSessionConfig.setIdleTime(IdleStatus.WRITER_IDLE,config.writerIdleTime)
    val connection = connector.connect(remoteAddress)
    connection.await.getSession
  }

  def initAcceptor(port: Int)(implicit config: Config): NioSocketAcceptor = {
    val acceptor = new NioSocketAcceptor
    acceptor.getFilterChain.addLast("codec", new ProtocolCodecFilter(codecFactory))

    acceptor.setHandler(new NodeHandler)

    acceptor.getSessionConfig.setReadBufferSize(config.readBufferSize)
    acceptor.getSessionConfig.setIdleTime(IdleStatus.READER_IDLE,config.readerIdleTime)
    acceptor.getSessionConfig.setIdleTime(IdleStatus.WRITER_IDLE,config.writerIdleTime)
    acceptor.bind(new InetSocketAddress(port))
    acceptor
  }
}
